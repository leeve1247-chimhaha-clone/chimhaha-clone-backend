package com.multirkh.chimhahaclone.minio;

import com.multirkh.chimhahaclone.util.IdGenerator;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

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
}
