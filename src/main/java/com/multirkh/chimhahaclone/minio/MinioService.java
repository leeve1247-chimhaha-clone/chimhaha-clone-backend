package com.multirkh.chimhahaclone.minio;

import com.multirkh.chimhahaclone.util.IdGenerator;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {
    @Value("${minio.export-url}")
    private String minioExportUrl;
    @Value("${minio.bucket-name}")
    private String minioBucketName;

    private final MinioClient minioClient;

    public String postFileWithRandomFileName(MultipartFile file){
        String randomImageName = IdGenerator.generateUniqueId();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioBucketName).object(
                                    randomImageName
                            ).stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return randomImageName;
    }

    public String getPreviewUrl(String randomImageName) {
        return minioExportUrl + "/api/v1/buckets/" + minioBucketName + "/objects/download?preview=true&prefix=" + randomImageName;
    }
}
