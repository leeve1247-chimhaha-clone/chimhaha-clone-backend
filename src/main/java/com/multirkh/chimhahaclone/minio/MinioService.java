package com.multirkh.chimhahaclone.minio;

import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.repository.ImageRepository;
import com.multirkh.chimhahaclone.service.image.resize.ImageResizerService;
import com.multirkh.chimhahaclone.service.image.resize.InputStreamAndLength;
import com.multirkh.chimhahaclone.util.IdGenerator;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final ImageResizerService imageResizerService;
    @Value("${minio.bucket-name}")
    private String minioBucketName;
    @Value("${minio.thumbnail-bucket-name}")
    private String thumbnailBucketName;

    private final MinioClient minioClient;

    private final ImageRepository imageRepository;

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

    public String getOrCreateUrl(String randomImageFileName) {
        Image image = imageRepository.findByFileName(randomImageFileName);
        if (image != null) {
            return image.getUrl();
        }
        try {
            return minioClient
                    .getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioBucketName)
                            .object(randomImageFileName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()).replace("http://minio-container:9000/","");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImages(Set<String> fileNames) {
        List<DeleteObject> objects = new LinkedList<>();
        for (String fileName : fileNames) {
            objects.add(new DeleteObject(fileName));
        }
        try {
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(minioBucketName).objects(objects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.out.println(
                        "Error in deleting object " + error.objectName() + "; " + error.message());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImage(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getImage(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createThumbnail(String postIdAndFileName, String contentType) {
        try {
            InputStream inputStream = getImage(postIdAndFileName.split("-")[1]);
            InputStreamAndLength isL = imageResizerService.resizeImage(inputStream, contentType);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(thumbnailBucketName)
                            .object(postIdAndFileName)
                            .stream(isL.inputStream(), isL.size(), -1)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteThumbnail(String postIdAndFileName) {
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(thumbnailBucketName)
                            .object(postIdAndFileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


