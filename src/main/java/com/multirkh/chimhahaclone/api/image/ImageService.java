package com.multirkh.chimhahaclone.api.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.image.dtos.PresignedPostDto;
import com.multirkh.chimhahaclone.api.image.dtos.PresignedUrlDTO;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.dto.PostReceived;
import com.multirkh.chimhahaclone.api.post.image.PostImageRepository;
import com.multirkh.chimhahaclone.api.post.image.domain.PostImage;
import com.multirkh.chimhahaclone.common.minio.MinioService;
import com.multirkh.chimhahaclone.common.util.IdGenerator;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;

@Service
@Transactional
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final MinioService minioService;
    private final PostImageRepository postImageRepository;

    public JsonNode applyPresignedUrlToImageSrc(JsonNode jsonContent) {
        JsonNode jsonNode = jsonContent.deepCopy();
        applyPresignedUrlToImageSrcRecursive(jsonNode);
        return jsonNode;
    }

    public PresignedUrlDTO getPresignedUrl() {
        String randomImageName = IdGenerator.generateUniqueId();
        while (imageRepository.findByFileName(randomImageName) != null) {
            randomImageName = IdGenerator.generateUniqueId();
        }
        return new PresignedUrlDTO(minioService.getPresignedUrl(randomImageName), randomImageName);
    }

    public PresignedPostDto getPresignedPost(MimeType mimeType){
        String randomImageName = IdGenerator.generateUniqueId();
        while (imageRepository.findByFileName(randomImageName) != null) {
            randomImageName = IdGenerator.generateUniqueId();
        }
        String fileName = String.join(".", randomImageName, mimeType.getSubtype());
        return new PresignedPostDto(minioService.getPresignedPost(fileName), fileName, minioService.getImageEndPointUrl());
    }

    public Set<String> getImageFileNameSet(JsonNode jsonContent) {
        return jsonContent.findParents("type").stream()
                .filter(jsonNode -> jsonNode.get("type").asText().equals("image"))
                .map(jsonNode -> jsonNode.get("altText").asText()).collect(Collectors.toSet());
    }

    @Transactional
    public void createPostImages(Post post) {
        Set<String> newImageNames = getImageFileNameSet(post.getJsonContent());
        if (newImageNames.isEmpty()) {
            return;
        }
        Set<Image> newImages = imageRepository.findByFileNames(newImageNames);
        createPostImages(post, newImages);
    }

    @Transactional
    public void updatePostImage(@NotNull Post post, @NotNull PostReceived request) {
        Set<PostImage> postImages = post.getPostImages();
        Set<String> prevImageFileNameSet = postImages.stream().map(postImage -> postImage.getImage().getFileName())
                .collect(Collectors.toSet());
        Set<String> updatedImageFileNameSet = getImageFileNameSet(request.getContent());

        Set<String> newImageNames = new HashSet<>(updatedImageFileNameSet);
        newImageNames.removeAll(prevImageFileNameSet);

        Set<Image> newImages = imageRepository.findByFileNames(newImageNames);
        createPostImages(post, newImages);

        Set<String> toBeDeleteImageNames = new HashSet<>(prevImageFileNameSet);
        toBeDeleteImageNames.removeAll(updatedImageFileNameSet);

        Set<Image> toBeDeleteImages = imageRepository.findByFileNames(toBeDeleteImageNames);
        deletePostImagesAndImagesIfAvailable(post, toBeDeleteImages);
    }

    @Transactional
    public void deletePostImage(Post post) {
        Set<Image> toBeDeleteImages = postImageRepository.findDistinctImageByPost(post);
        deletePostImagesAndImagesIfAvailable(post, toBeDeleteImages);

    }

    @Transactional
    protected void createPostImages(@NotNull Post post, Set<Image> newImages) {
        Set<PostImage> newPostImageSet = new HashSet<>();
        for (Image newImage : newImages) {
            PostImage postImage = new PostImage(post, newImage);
            newImage.getPostImages().add(postImage);
            newPostImageSet.add(postImage);
            imageRepository.save(newImage);
        }
        postImageRepository.saveAll(newPostImageSet);
        post.addPostImages(newPostImageSet);
    }

    @Transactional
    protected void deletePostImagesAndImagesIfAvailable(Post post, Set<Image> toBeDeleteImages) {
        Set<PostImage> toBeDeletePostImages = postImageRepository.findAllByPostAndImageIn(post, toBeDeleteImages);
        Set<Image> trash = new HashSet<>();
        for (Image toBeDeleteImage : toBeDeleteImages) {
            toBeDeleteImage.getPostImages().removeAll(toBeDeletePostImages);
            if (toBeDeleteImage.getPostImages().isEmpty()) {
                trash.add(toBeDeleteImage);
            }
        }
        post.removePostImages(toBeDeletePostImages);
        postImageRepository.deleteAllByPostImages(toBeDeletePostImages);
        imageRepository.deleteAllByImages(trash);
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
                    objNode.put("src", srcUrl);
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

    private String getThumbnailImageFileName(JsonNode content) {
        Optional<String> first = content.findParents("type").stream()
                .filter(t -> t.get("type").asText().equals("image")).map(t -> t.get("altText").asText()).findFirst();
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
        if (rawImage.getThumbNailImage() != null) {
            return rawImage.getThumbNailImage();
        }
        String srcUrl = minioService.createThumbnail(rawImage.getFileName());
        Image thumbNailImage = new Image(rawImage, srcUrl, ZonedDateTime.now().plusHours(167));
        rawImage.setThumbNailImage(thumbNailImage);
        return imageRepository.save(thumbNailImage);
    }

    @Transactional
    public void createThumbnailImage(@NotNull Post post) {
        String titleImageFileName = getThumbnailImageFileName(post.getJsonContent());
        if (titleImageFileName == null) {
            return;
        }
        Image rawImage = imageRepository.findByFileName(titleImageFileName);
        Image thumbnailImage = getOrCreateThumbnail(rawImage);
        post.addThumbNailImage(thumbnailImage);
    }

    @Transactional
    public void updateThumbnailImage(@NotNull Post post, @NotNull PostReceived request) {
        String newThumbNailImageFileName = getThumbnailImageFileName(request.getContent());
        Image oldThumbNailImage = post.getThumbNailImage();

        // Exist -> Null
        if (oldThumbNailImage != null && newThumbNailImageFileName == null) {
            post.removeThumbNailImage();
            if (oldThumbNailImage.getThumbNailedPost().isEmpty()) {
                minioService.deleteThumbnail(oldThumbNailImage.getRawImage().getFileName());
                oldThumbNailImage.getRawImage().setThumbNailImage(null);
                imageRepository.delete(oldThumbNailImage);
            }
            return;
        }

        // Same (null)
        if (oldThumbNailImage == null && newThumbNailImageFileName == null) {
            return;
        }

        // Null -> Exist
        Image newRawImage = imageRepository.findByFileName(newThumbNailImageFileName);
        Image newThumbNailImage = getOrCreateThumbnail(newRawImage);
        if (oldThumbNailImage == null) {
            newThumbNailImage.getThumbNailedPost().add(post);
            post.addThumbNailImage(newThumbNailImage);
            return;
        }

        // Same (not null)
        if (oldThumbNailImage.equals(newThumbNailImage)) {
            return;
        }

        // Changed
        post.removeThumbNailImage();
        if (oldThumbNailImage.getThumbNailedPost().isEmpty()) {
            minioService.deleteThumbnail(oldThumbNailImage.getRawImage().getFileName());
            oldThumbNailImage.getRawImage().setThumbNailImage(null);
            imageRepository.delete(oldThumbNailImage);
        }
        newThumbNailImage.getThumbNailedPost().add(post);
        post.addThumbNailImage(newThumbNailImage);
    }

    @Transactional
    public void deleteThumbnailImage(@NotNull Post post) {
        Image thumbNailImage = post.getThumbNailImage();
        if (thumbNailImage == null) {
            return;
        }
        post.removeThumbNailImage();
        if (thumbNailImage.getThumbNailedPost().isEmpty()) {
            minioService.deleteThumbnail(thumbNailImage.getRawImage().getFileName());
            thumbNailImage.getRawImage().setThumbNailImage(null);
        }
    }
}
