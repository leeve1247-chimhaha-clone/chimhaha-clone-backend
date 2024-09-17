package com.multirkh.chimhahaclone.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class MinioConfig {
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.bucket-name}")
    private String minioBucketName;

    @Value("${minio.thumbnail-bucket}")
    private String minioThumbNailBucket;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minioUrl)
                            .credentials(minioAccessKey, minioSecretKey)
                            .build();
            Map<String, Boolean> bucketMap = new HashMap<>();

            bucketMap.put(minioBucketName, minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build()));
            bucketMap.put(minioThumbNailBucket, minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioThumbNailBucket).build()));
            for (Map.Entry<String, Boolean> entry : bucketMap.entrySet()) {
                String bucketName = entry.getKey();
                boolean found = entry.getValue();
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                } else {
                    log.info("Bucket '{}' already exists.", bucketName);
                }
            }
            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating minio client", e);
        }
    }
}
