package com.multirkh.chimhahaclone.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.enums.ImageStatus;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
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

    public void createPostImages(Post post) {
        Set<String> imageUrls = getImageUrls(post.getJsonContent());
        if (imageUrls.isEmpty()) return;

        Set<PostImage> postImages = new HashSet<>();
        Set<Image> images = imageRepository.findByFileNames(imageUrls);
        for (Image image : images) {
            PostImage postImage = new PostImage(post, image, ImageStatus.POSTED);
            postImages.add(postImage);
            if (image.getFileName().equals(post.getTitleImageFileName())) {
                postImage.setMainImage(true);
                // minio 이미지 thumbnail 생성
                minioService.createThumbnail(post.getId() + "-" + image.getFileName(), image.getContentType());
            }
        }
        post.getPostImages().addAll(postImages);
    }

    public void updatePostImage(Post post, JsonNode jsonContent, @Nullable String titleImageFileName) {
        Set<PostImage> postImages = postImageRepository.findByPost(post);
        Set<String> newImageUrls = getImageUrls(jsonContent);

        // 썸네일 이미지 처리
        String prevThumbnailFileName = post.getTitleImageFileName();
        if (prevThumbnailFileName == null && titleImageFileName != null){
            Image image = imageRepository.findByFileName(titleImageFileName);
            minioService.createThumbnail(post.getId() + "-" + titleImageFileName, image.getContentType());
        } else if (prevThumbnailFileName != null && titleImageFileName == null){
            minioService.deleteThumbnail(post.getId() + "-" + prevThumbnailFileName);
        } else if (prevThumbnailFileName != null && !prevThumbnailFileName.equals(titleImageFileName)){
            Image image = imageRepository.findByFileName(titleImageFileName);
            minioService.deleteThumbnail(post.getId() + "-" + prevThumbnailFileName);
            minioService.createThumbnail(post.getId() + "-" + titleImageFileName, image.getContentType());
        }

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

    public void deletePostImage(Post post) {
        Set<PostImage> postImages = postImageRepository.findByPost(post);
        String titleImageFileName = post.getTitleImageFileName();
        if (titleImageFileName != null) {
            minioService.deleteThumbnail(post.getId() + "-" + titleImageFileName);
        }
        Set<Image> deletedImages = new HashSet<>();
        postImages.forEach(postImage -> {
            Image image = postImage.getImage();
            Set<PostImage> postImagesOfImage = image.getPostImages();
            postImagesOfImage.remove(postImage);
            if (postImagesOfImage.isEmpty()) {
                //minio 이미지 삭제
                deletedImages.add(image);
            }
        });
        imageRepository.deleteAllByImages(deletedImages);
        minioService.deleteImages(deletedImages.stream().map(Image::getFileName).collect(Collectors.toSet()));
    }

    @Scheduled(fixedRate = 1000 * 60 * 15) // 15분마다 실행
    public void deleteUnusedImages() {
        Set<Image> imagesEditedBefore = imageRepository.findImagesEditedBefore(ZonedDateTime.now().minusDays(1));
        if (imagesEditedBefore.isEmpty()) return;
        minioService.deleteImages(imagesEditedBefore.stream().map(Image::getFileName).collect(Collectors.toSet()));
        imageRepository.deleteAllByImages(imagesEditedBefore);
    }

    public void validateImage(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("file is empty");
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) throw new IllegalArgumentException("file is not image");
    }

    public String createImage(MultipartFile file) {
        String randomImageName = minioService.postFileWithRandomFileName(file);
        imageRepository.save(new Image(randomImageName, file.getContentType()));
        return minioService.getPreviewUrl(randomImageName);
    }
}
