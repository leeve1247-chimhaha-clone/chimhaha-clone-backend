package com.multirkh.chimhahaclone.common.minio;

import com.multirkh.chimhahaclone.api.image.resize.ImageResizerService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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

    public String getPresignedUrl(String randomImageName) {
        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.PUT)
                                    .bucket(minioBucketName)
                                    .object(randomImageName)
                                    .expiry(15, TimeUnit.MINUTES)
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

    public String createThumbnail(String fileName) {
        try {
            String mimeType = getType(fileName);
            InputStream rawImage = getImage(fileName);
            InputStream resizedImageInputStream = imageResizerService.createResizedImage(rawImage);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(thumbnailBucketName)
                            .object(fileName)
                            .stream(resizedImageInputStream, -1, 10485760)
                            .contentType(mimeType)
                            .build());
            return createOrRenewThumbNailUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteThumbnail(String fileName) {
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(thumbnailBucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createOrRenewUrl(String fileName) {
        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(minioBucketName)
                                    .object(fileName)
                                    .expiry(7, TimeUnit.DAYS)
                                    .build()).replace("http://minio-container:9000/","");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createOrRenewThumbNailUrl(String fileName){
        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(thumbnailBucketName)
                                    .object(fileName)
                                    .expiry(7, TimeUnit.DAYS)
                                    .build()).replace("http://minio-container:9000/","");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getType(String fileName){
        try{
            StatObjectResponse statObjectResponse = minioClient
                    .statObject(StatObjectArgs
                            .builder()
                            .bucket(minioBucketName)
                            .object(fileName)
                            .build()
                    );
            return statObjectResponse.contentType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


