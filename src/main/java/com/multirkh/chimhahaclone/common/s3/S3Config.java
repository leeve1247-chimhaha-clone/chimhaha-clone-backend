package com.multirkh.chimhahaclone.common.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CORSConfiguration;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class S3Config {

    @Value("${spa.web.origin}")
    private String spaUrl;

    @Value("${s3.export-url}")
    private String s3ExportUrl;

    @Value("${s3.access-key}")
    private String s3AccessKey;

    @Value("${s3.secret-key}")
    private String s3SecretKey;

    @Value("${s3.bucket-name}")
    private String s3BucketName;

    @Value("${s3.thumbnail-bucket-name}")
    private String thumbnailBucketName;

    final ObjectMapper objectMapper;

    public S3Config(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public S3Client s3Client() {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(s3ExportUrl))
                .region(Region.of("us-east-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3AccessKey, s3SecretKey)))
                .forcePathStyle(true)
                .build();

        ensureBucketExists(client, s3BucketName);
        ensureBucketExists(client, thumbnailBucketName);
        initBucketPolicy(client);
        setupCors(client, s3BucketName);
        setupCors(client, thumbnailBucketName);

        return client;
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(s3ExportUrl))
                .region(Region.of("us-east-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3AccessKey, s3SecretKey)))
                .build();
    }

    private void ensureBucketExists(S3Client client, String bucketName) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' already exists.", bucketName);
        } catch (NoSuchBucketException e) {
            client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' created.", bucketName);
        }
    }

    private void initBucketPolicy(S3Client client) {
        try {
            String policyJson = createPublicReadAccessJsonPolicy(s3BucketName);
            client.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(s3BucketName)
                    .policy(policyJson)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set bucket policy", e);
        }
    }

    private String createPublicReadAccessJsonPolicy(String bucketName) {
        try {
            Map<String, Object> policyMap = Map.of(
                    "Version", "2012-10-17",
                    "Statement", List.of(
                            Map.of(
                                    "Sid", "PublicReadGetObject",
                                    "Effect", "Allow",
                                    "Principal", "*",
                                    "Action", List.of("s3:GetObject"),
                                    "Resource", List.of("arn:aws:s3:::" + bucketName + "/*")
                            )
                    )
            );
            return objectMapper.writeValueAsString(policyMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupCors(S3Client client, String bucketName) {
        try {
            client.deleteBucketCors(DeleteBucketCorsRequest.builder().bucket(bucketName).build());
        } catch (Exception ignored) {
        }

        CORSRule corsRule = CORSRule.builder()
                .allowedOrigins(spaUrl)
                .allowedMethods("PUT", "POST")
                .allowedHeaders("*")
                .maxAgeSeconds(3000)
                .build();

        client.putBucketCors(PutBucketCorsRequest.builder()
                .bucket(bucketName)
                .corsConfiguration(CORSConfiguration.builder()
                        .corsRules(corsRule)
                        .build())
                .build());
    }
}
