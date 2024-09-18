package com.multirkh.chimhahaclone.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MockFileToMinioService {
    @Value("${minio.bucket-name}")
    private String minioBucketName;

    private final MinioClient minioClient;

    public void postMockFile(String fileName) {
        File file = new File("src/test/resources/test.png");
        try(InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioBucketName).object(
                                    fileName
                            ).stream(inputStream, file.length(), -1)
                            .contentType("image/png")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

