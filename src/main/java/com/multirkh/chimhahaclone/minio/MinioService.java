package com.multirkh.chimhahaclone.minio;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.util.IdGenerator;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetObjectTagsArgs;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MinioService {
    @Value("${minio.export-url}")
    private String minioExportUrl;
    @Value("${minio.bucket-name}")
    private String minioBucketName;

    private final MinioClient minioClient;

    public String postFileWithRandomFileName(@NotNull MultipartFile file) {
        String randomImageName = IdGenerator.generateUniqueId();
        String randomImageFileName = randomImageName + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioBucketName).object(
                                    randomImageFileName
                            ).stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return randomImageFileName;
    }

    // randomImageFileName : "파일이름 명.확장자" ex : "this_is_random_string_20.png"
    public String getPreviewUrl(String randomImageFileName) {
        return minioExportUrl + "/api/v1/buckets/" + minioBucketName + "/objects/download?preview=true&prefix=" + randomImageFileName;
    }

    void setTagPosted(String ImageFileName) {
        try {
            Map<String, String> tags = new HashMap<>();
            tags.put("posted", "true");
            minioClient.setObjectTags(SetObjectTagsArgs.builder()
                    .bucket(minioBucketName)
                    .object(ImageFileName)
                    .tags(tags)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setImageTagPosted(@NotNull JsonNode jsonNode) {
        Set<String> imageUrls = new HashSet<>();
        jsonNode.findValues("image").forEach(json -> {
            imageUrls.add(json.asText()
                    .split("preview=true&prefix=")[1]);
        });
        for (String imageUrl : imageUrls) {
            setTagPosted(imageUrl);
        }
    }
}
