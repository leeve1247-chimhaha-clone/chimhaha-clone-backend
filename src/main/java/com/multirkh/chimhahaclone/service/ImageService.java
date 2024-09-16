package com.multirkh.chimhahaclone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.ImageStatus;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import com.multirkh.chimhahaclone.minio.MinioService;
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

    public ImageService(ImageRepository imageRepository, PostImageRepository postImageRepository, MinioService minioService) {
        this.imageRepository = imageRepository;
    }

    public Set<String> getImageUrls(JsonNode jsonContent) {
        Set<String> imageUrls = new HashSet<>();
        jsonContent.findValues("image").forEach(image -> {
            imageUrls.add(image.asText().split("preview=true&prefix=")[1]);
        });
        return imageUrls;
    }

    public Set<PostImage> createPostImages(Post post, Set<String> imageUrls) {
        Set<PostImage> postImages = new HashSet<>();
        Set<Image> images = imageRepository.findByFileNames(imageUrls);
        for (Image image : images) {
            PostImage postImage = new PostImage(post, image, ImageStatus.POSTED);
            postImages.add(postImage);
        }
        return postImages;
    }
}
