package com.multirkh.chimhahaclone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.ImageStatus;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import com.multirkh.chimhahaclone.repository.ImageRepository;
import com.multirkh.chimhahaclone.repository.PostImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final PostImageRepository postImageRepository;

    public ImageService(ImageRepository imageRepository, PostImageRepository postImageRepository) {
        this.imageRepository = imageRepository;
        this.postImageRepository = postImageRepository;
    }

    public Set<String> getImageUrls(JsonNode jsonContent) {
        Set<String> imageUrls = new HashSet<>();
        jsonContent.findValues("image").forEach(image -> {
            imageUrls.add(image.asText().split("preview=true&prefix=")[1]);
        });
        return imageUrls;
    }

    public void createPostImages(Post post, JsonNode jsonContent) {
        Set<String> imageUrls = getImageUrls(jsonContent);
        Set<PostImage> postImages = new HashSet<>();
        if (imageUrls.isEmpty()) return;
        Set<Image> images = imageRepository.findByFileNames(imageUrls);
        for (Image image : images) {
            PostImage postImage = new PostImage(post, image, ImageStatus.POSTED);
            postImages.add(postImage);
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
}
