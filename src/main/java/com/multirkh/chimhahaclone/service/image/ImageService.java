package com.multirkh.chimhahaclone.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import com.multirkh.chimhahaclone.minio.MinioService;
import com.multirkh.chimhahaclone.repository.ImageRepository;
import com.multirkh.chimhahaclone.repository.PostImageRepository;
import com.multirkh.chimhahaclone.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    private final PostImageRepository postImageRepository;
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

    @Transactional
    public void createPostImages(Post post) {
        Set<String> imageFileNameSet = getImageFileNameSet(post.getJsonContent());
        if (imageFileNameSet.isEmpty()) return;
        Set<Image> images = imageRepository.findByFileNames(imageFileNameSet);
        post.addPostImages(images);
    }

    @Transactional
    public void updatePostImage(@NotNull Post post, @NotNull PostReceived request) {
        log.info("Updating post image");
        Set<PostImage> postImages = post.getPostImages();
        Set<String> prevImageFileNameSet = postImages.stream().map(postImage -> postImage.getImage().getFileName()).collect(Collectors.toSet());
        Set<String> updatedImageFileNameSet = getImageFileNameSet(request.getContent());

        log.info("=================");
        Set<String> newImageNames = new HashSet<>(updatedImageFileNameSet);
        newImageNames.removeAll(prevImageFileNameSet);

        log.info("======2===========");
        Set<Image> newImages = imageRepository.findByFileNames(newImageNames);
        post.addPostImages(newImages);

        log.info("======23===========");
        Set<String> toBeDeleteImageNames = new HashSet<>(prevImageFileNameSet);
        toBeDeleteImageNames.removeAll(updatedImageFileNameSet);

        log.info("======25===========");
        Set<Image> toBeDeleteImages = imageRepository.findByFileNames(toBeDeleteImageNames);
        Set<PostImage> toBeDeletePostImages = post.getPostImages().stream().filter(postImage -> toBeDeleteImages.contains(postImage.getImage())).collect(Collectors.toSet());
        postImageRepository.deleteAll(toBeDeletePostImages);
        post.removePostImages(toBeDeleteImages);
        log.info("======26===========");
        for (Image image: toBeDeleteImages){
            log.info("======291===========");
            if (!image.getPostImages().isEmpty()) continue;
            log.info("======292===========");
            minioService.deleteImage(image.getFileName());
            imageRepository.delete(image);
        }
        log.info("======29===========");
    }

    @Transactional
    public void deletePostImage(Post post) {
        Set<Image> toBeDeleteImages = post.getPostImages().stream().map(PostImage::getImage).collect(Collectors.toSet());
        Set<PostImage> toBeDeletePostImages = post.getPostImages().stream().filter(postImage -> toBeDeleteImages.contains(postImage.getImage())).collect(Collectors.toSet());
        postImageRepository.deleteAll(toBeDeletePostImages);
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

    @Transactional
    public void createThumbnailImage(@NotNull Post post) {
        String titleImageFileName = getTitleImageFileName(post.getJsonContent());
        if (titleImageFileName == null) return;
        Image rawImage = imageRepository.findByFileName(titleImageFileName);
        Image thumbnailImage = getOrCreateThumbnail(rawImage);
        thumbnailImage.getThumbNailedPost().add(post);
        post.setThumbNailImage(thumbnailImage);
    }

    @Transactional
    public void updateThumbnailImage(@NotNull Post post, @NotNull PostReceived request) {
        String titleImageFileName = getTitleImageFileName(request.getContent());
        Image prevThumbNailImage = post.getThumbNailImage();

        // Exist -> Null
        if (prevThumbNailImage != null && titleImageFileName == null) {
            log.atInfo().log("ThumbNail image has deleted");
            prevThumbNailImage.getThumbNailedPost().remove(post);
            if (prevThumbNailImage.getThumbNailedPost().isEmpty()) {
                if (prevThumbNailImage.getRawImage() != null) {
                    minioService.deleteThumbnail(prevThumbNailImage.getRawImage().getFileName());
                } else {
                    log.atWarn().log("RawImage is null for image id: {}", prevThumbNailImage.getId());
                }
                post.setThumbNailImage(null);
                imageRepository.delete(prevThumbNailImage);
            }
            return;
            }


        // Same (null)
        if (prevThumbNailImage == null && titleImageFileName == null){
            log.atInfo().log("ThumbNail image is Same(null)");
            return;
        }

        // Null -> Exist
        Image updatedRawImage = imageRepository.findByFileName(titleImageFileName);
        Image thumbnailImage = getOrCreateThumbnail(updatedRawImage);
        if (prevThumbNailImage == null) {
            log.atInfo().log("ThumbNail image has created");
            thumbnailImage.getThumbNailedPost().add(post);
            post.setThumbNailImage(thumbnailImage);
            return;
        }

        // Same (not null)
        if (prevThumbNailImage.equals(thumbnailImage)) {
            log.atInfo().log("ThumbNail image is Same(not null)");
            return;
        }

        // Changed
        prevThumbNailImage.getThumbNailedPost().remove(post);
        log.atInfo().log("ThumbNail image has been Changed");
        log.atInfo().log("prevThumbNailImage's post is now " + String.valueOf(prevThumbNailImage.getThumbNailedPost().size()));
        if (prevThumbNailImage.getThumbNailedPost().isEmpty()) {
            minioService.deleteThumbnail(prevThumbNailImage.getRawImage().getFileName());
            post.setThumbNailImage(null);
            imageRepository.delete(prevThumbNailImage);
        }
        thumbnailImage.getThumbNailedPost().add(post);
        post.setThumbNailImage(thumbnailImage);
    }

    @Transactional
    public void deleteThumbnailImage(@NotNull Post post){
        Image thumbNailImage = post.getThumbNailImage();
        thumbNailImage.getThumbNailedPost().remove(post);
        if (thumbNailImage.getThumbNailedPost().isEmpty()) {
            log.atInfo().log("ThumbNail image has deleted");
            thumbNailImage.getThumbNailedPost().remove(post);
            if (thumbNailImage.getThumbNailedPost().isEmpty()) {
                if (thumbNailImage.getRawImage() != null) {
                    minioService.deleteThumbnail(thumbNailImage.getRawImage().getFileName());
                } else {
                    log.atWarn().log("RawImage is null for image id: {}", thumbNailImage.getId());
                }
                imageRepository.delete(thumbNailImage);
            }
            return;
        }
    }
}
