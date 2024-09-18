package com.multirkh.chimhahaclone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.ImageStatus;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import com.multirkh.chimhahaclone.minio.MinioService;
import com.multirkh.chimhahaclone.repository.ImageRepository;
import com.multirkh.chimhahaclone.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final PostImageRepository postImageRepository;
    private final MinioService minioService;

    public Set<String> getImageUrls(JsonNode jsonContent) {
        Set<String> imageUrls = new HashSet<>();
        jsonContent.findValues("image").forEach(image -> imageUrls.add(image.asText().split("preview=true&prefix=")[1]));
        return imageUrls;
    }

    public void createPostImages(Post post, JsonNode jsonContent, String titleImageFileName) {
        Set<String> imageUrls = getImageUrls(jsonContent);
        if (imageUrls.isEmpty()) return;

        Set<PostImage> postImages = new HashSet<>();
        Set<Image> images = imageRepository.findByFileNames(imageUrls);
        for (Image image : images) {
            PostImage postImage = new PostImage(post, image, ImageStatus.POSTED);
            postImages.add(postImage);
            if (image.getFileName().equals(titleImageFileName)) {
                postImage.setMainImage(true);
                // minio 이미지 thumbnail 생성
                minioService.createThumbnail(post.getId() + "-" + image.getFileName(), image.getContentType());
            }
        }
        post.getPostImages().addAll(postImages);
    }

    public void updatePostImage(Post post, JsonNode jsonContent) {
        Set<PostImage> postImages = postImageRepository.findByPost(post);
        Set<String> newImageUrls = getImageUrls(jsonContent);

        Set<PostImage> deletedPostImages = new HashSet<>();
        Set<Image> deletedImages = new HashSet<>();
        postImages.forEach(postImage -> {
            Image image = postImage.getImage();
            if (!newImageUrls.contains(image.getFileName())) {
                Set<PostImage> postImagesOfImage = image.getPostImages();
                postImagesOfImage.remove(postImage);
                if (postImagesOfImage.isEmpty()) {
                    //minio 이미지 삭제
                    minioService.deleteImage(image.getFileName());
                    deletedImages.add(image);
                }
                deletedPostImages.add(postImage);
            } else {
                postImage.setStatus(ImageStatus.POSTED);
                newImageUrls.remove(image.getFileName());
            }
        });

        postImageRepository.deleteAllByPostImages(deletedPostImages);
        imageRepository.deleteAllByImages(deletedImages);

        postImages.removeAll(deletedPostImages);
        Set<Image> images = imageRepository.findByFileNames(newImageUrls);
        for (Image image : images) {
            PostImage postImage = new PostImage(post, image, ImageStatus.POSTED);
            postImages.add(postImage);
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 15) // 15분마다 실행
    public void deleteUnusedImages() {
        Set<Image> imagesEditedBefore = imageRepository.findImagesEditedBefore(ZonedDateTime.now().minusDays(1));
        if (imagesEditedBefore.isEmpty()) return;
        minioService.deleteImages(imagesEditedBefore.stream().map(Image::getFileName).collect(Collectors.toSet()));
        imageRepository.deleteAllByImages(imagesEditedBefore);
    }
}
