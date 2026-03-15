package com.multirkh.chimhahaclone.common.s3;

import com.multirkh.chimhahaclone.api.image.resize.ImageResizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PostObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPostRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PostObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final ImageResizerService imageResizerService;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    private String s3BucketName;

    @Value("${s3.thumbnail-bucket-name}")
    private String thumbnailBucketName;

    @Value("${s3.export-url}")
    private String exportUrl;

    public String getPresignedUrl(String randomImageName) {
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(s3BucketName)
                        .key(randomImageName)
                        .build())
                .build();
        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return presigned.url().toString();
    }

    public String getImageEndPointUrl() {
        return String.join("/", List.of(exportUrl, s3BucketName));
    }

    public PresignedPostRequest getPresignedPost(String fileName) {
        PostObjectPresignRequest presignRequest = PostObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .postObjectRequest(PostObjectRequest.builder()
                        .bucket(s3BucketName)
                        .key(fileName)
                        .build())
                .build();
        return s3Presigner.presignPostObject(presignRequest);
    }

    public void deleteImages(Set<String> fileNames) {
        List<ObjectIdentifier> objects = fileNames.stream()
                .map(name -> ObjectIdentifier.builder().key(name).build())
                .collect(Collectors.toList());
        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(s3BucketName)
                .delete(Delete.builder().objects(objects).build())
                .build());
    }

    public void deleteImage(String fileName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3BucketName)
                .key(fileName)
                .build());
    }

    public InputStream getImage(String fileName) {
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(s3BucketName)
                        .key(fileName)
                        .build(),
                ResponseTransformer.toInputStream());
        return response;
    }

    public String createThumbnail(String fileName) {
        try {
            String mimeType = getType(fileName);
            InputStream rawImage = getImage(fileName);
            InputStream resizedImageInputStream = imageResizerService.createResizedImage(rawImage);
            byte[] imageBytes = resizedImageInputStream.readAllBytes();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(thumbnailBucketName)
                            .key(fileName)
                            .contentType(mimeType)
                            .contentLength((long) imageBytes.length)
                            .build(),
                    RequestBody.fromBytes(imageBytes));
            return createOrRenewThumbNailUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteThumbnail(String fileName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(thumbnailBucketName)
                .key(fileName)
                .build());
    }

    public String createOrRenewUrl(String fileName) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(s3BucketName)
                        .key(fileName)
                        .build())
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String createOrRenewThumbNailUrl(String fileName) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(thumbnailBucketName)
                        .key(fileName)
                        .build())
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String getType(String fileName) {
        return s3Client.headObject(HeadObjectRequest.builder()
                .bucket(s3BucketName)
                .key(fileName)
                .build())
                .contentType();
    }

    public String getEndPointUrl() {
        return exportUrl;
    }

    public String getImageBucket() {
        return s3BucketName;
    }
}
