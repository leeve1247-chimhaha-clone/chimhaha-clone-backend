package com.multirkh.chimhahaclone.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import com.multirkh.chimhahaclone.minio.MinioService;
import com.multirkh.chimhahaclone.repository.ImageRepository;
import com.multirkh.chimhahaclone.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final MinioService minioService;
    @Value("${minio.export-url}")
    private String minioPublicUrl;

    public JsonNode applyPresignedUrlToImageSrc(JsonNode jsonContent) {
        JsonNode jsonNode = jsonContent.deepCopy();
        applyPresignedUrlToImageSrcRecursive(jsonNode);
        return jsonNode;
    }

    public PresignedUrlDTO getPresignedUrl() {
        String randomImageName = IdGenerator.generateUniqueId();
        if (imageRepository.findByFileName(randomImageName) != null) {
            return getPresignedUrl();
        }
        return new PresignedUrlDTO(minioService.getPresignedUrl(randomImageName), randomImageName);
    }

    public Set<String> getImageFileNameSet(JsonNode jsonContent) {
        return jsonContent.findParents("type").stream().filter(jsonNode -> jsonNode.get("type").asText().equals("image")).map(jsonNode -> jsonNode.get("altText").asText()).collect(Collectors.toSet());
    }

    public void createPostImages(Post post) {
        Set<String> imageFileNameSet = getImageFileNameSet(post.getJsonContent());
        if (imageFileNameSet.isEmpty()) return;
        Set<Image> images = imageRepository.findByFileNames(imageFileNameSet);
        post.addPostImages(images);
    }

    public void updatePostImage(Post post, PostReceived request) {
        Set<PostImage> postImages = post.getPostImages();
        Set<String> prevImageFileNameSet = postImages.stream().map(postImage -> postImage.getImage().getFileName()).collect(Collectors.toSet());
        Set<String> updatedImageFileNameSet = getImageFileNameSet(request.getContent());

        Set<String> newImageNames = new HashSet<>(updatedImageFileNameSet);
        newImageNames.removeAll(prevImageFileNameSet);

        Set<Image> newImages = imageRepository.findByFileNames(newImageNames);
        post.addPostImages(newImages);

        Set<String> toBeDeleteImageNames = new HashSet<>(prevImageFileNameSet);
        toBeDeleteImageNames.removeAll(updatedImageFileNameSet);

        Set<Image> toBeDeleteImages = imageRepository.findByFileNames(toBeDeleteImageNames);
        post.removePostImages(toBeDeleteImages);
        for (Image image: toBeDeleteImages){
            if (!image.getPostImages().isEmpty()) continue;
            minioService.deleteImage(image.getFileName());
            imageRepository.delete(image);
        }
    }

    public void deletePostImage(Post post) {
        Set<Image> toBeDeleteImages = post.getPostImages().stream().map(PostImage::getImage).collect(Collectors.toSet());
        post.removePostImages(toBeDeleteImages);
        for (Image image: toBeDeleteImages){
            if (!image.getPostImages().isEmpty()) continue;
            minioService.deleteImage(image.getFileName());
            imageRepository.delete(image);
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 15) // 15분마다 실행
    public void deleteUnusedImages() {
        Set<Image> imagesEditedBefore = imageRepository.findImagesEditedBefore(ZonedDateTime.now().minusDays(1));
        if (imagesEditedBefore.isEmpty()) return;
        minioService.deleteImages(imagesEditedBefore.stream().map(Image::getFileName).collect(Collectors.toSet()));
        imageRepository.deleteAllByImages(imagesEditedBefore);
    }

    public String getSrcUrl(String fileName) {
        Image image = imageRepository.findByFileName(fileName);
        if (image == null) {
            String srcUrl = minioService.createOrRenewUrl(fileName);
            String contentType = minioService.getType(fileName);
            imageRepository.save(new Image(
                    fileName,
                    contentType,
                    srcUrl,
                    ZonedDateTime.now().plusHours(167)
            ));
            return srcUrl;
        }
        if (image.getExpirationDate().isBefore(ZonedDateTime.now().plusHours(1))) {
            String srcUrl = minioService.createOrRenewUrl(fileName);
            image.setUrl(srcUrl);
        }
        return image.getUrl();
    }

    private void applyPresignedUrlToImageSrcRecursive(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            JsonNode typeNode = objNode.get("type");
            if (typeNode != null && typeNode.isTextual() && "image".equals(typeNode.asText())) {
                if (objNode.has("altText") && objNode.has("src")) {
                    String srcUrl = getSrcUrl(objNode.get("altText").asText());
                    objNode.put("src", minioPublicUrl + "/" + srcUrl);
                }
            }
            objNode.fieldNames().forEachRemaining(fieldName -> {
                applyPresignedUrlToImageSrcRecursive(objNode.get(fieldName));
            });
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                applyPresignedUrlToImageSrcRecursive(element);
            }
        }
    }

    private String getTitleImageFileName(JsonNode content) {
        Optional<String> first = content.findParents("type").stream().filter(t -> t.get("type").asText().equals("image")).map(t -> t.get("altText").asText()).findFirst();
        return first.orElse(null);
    }

    public String getThumbnailSrcUrl(String fileName) {
        Image rawImage = imageRepository.findByFileName(fileName);
        Image thumbNailImage = getOrCreateThumbnail(rawImage);
        if (thumbNailImage.getExpirationDate().isBefore(ZonedDateTime.now().plusHours(1))) {
            String renewedUrl = minioService.createOrRenewUrl(fileName);
            thumbNailImage.setUrl(renewedUrl);
        }
        return thumbNailImage.getUrl();
    }

    public Image getOrCreateThumbnail(Image rawImage) {
        if (rawImage.getThumbNailImage() != null) return rawImage.getThumbNailImage();
        String srcUrl =  minioService.createThumbnail(rawImage.getFileName());
        Image thumbNailImage = new Image(rawImage, srcUrl, ZonedDateTime.now().plusHours(167));
        rawImage.setThumbNailImage(thumbNailImage);
        return imageRepository.save(thumbNailImage);
    }

    public void createThumbnailImage(Post post) {
        String titleImageFileName = getTitleImageFileName(post.getJsonContent());
        if (titleImageFileName == null) return;
        Image rawImage = imageRepository.findByFileName(titleImageFileName);
        Image thumbnailImage = getOrCreateThumbnail(rawImage);
        thumbnailImage.getThumbNailedPost().add(post);
        post.setThumbNailImage(thumbnailImage);
    }

    public void updateThumbnailImage(Post post, PostReceived request) {
        String titleImageFileName = getTitleImageFileName(request.getContent());
        Image prevThumbNailImage = post.getThumbNailImage();

        // Exist -> Null
        if (prevThumbNailImage != null && titleImageFileName == null) {
            prevThumbNailImage.getThumbNailedPost().remove(post);
            if (prevThumbNailImage.getThumbNailedPost().isEmpty()) {
                minioService.deleteThumbnail(prevThumbNailImage.getRawImage().getFileName());
                imageRepository.delete(prevThumbNailImage);
            };
            return;
        }


        // Same (null)
        if (prevThumbNailImage == null && titleImageFileName == null){
            return;
        }

        // Null -> Exist
        Image updatedThumbNailImage = imageRepository.findByFileName(titleImageFileName);
        if (prevThumbNailImage == null) {
            Image thumbnailImage = getOrCreateThumbnail(updatedThumbNailImage);
            thumbnailImage.getThumbNailedPost().add(post);
            post.setThumbNailImage(thumbnailImage);
            return;
        }

        // Same (not null)
        if (prevThumbNailImage.equals(updatedThumbNailImage)) {
            return;
        }

        // Changed
        prevThumbNailImage.getThumbNailedPost().remove(post);
        if (prevThumbNailImage.getThumbNailedPost().isEmpty()) {
            minioService.deleteThumbnail(prevThumbNailImage.getRawImage().getFileName());
            imageRepository.delete(prevThumbNailImage);
        }
        updatedThumbNailImage.getThumbNailedPost().add(post);
        post.setThumbNailImage(updatedThumbNailImage);
    }

    public void deleteThumbnailImage(Post post){
        Image thumbNailImage = post.getThumbNailImage();
        thumbNailImage.getThumbNailedPost().remove(post);
        if (thumbNailImage.getThumbNailedPost().isEmpty()) {
            minioService.deleteThumbnail(thumbNailImage.getRawImage().getFileName());
            imageRepository.delete(thumbNailImage);
        }
    }
}
