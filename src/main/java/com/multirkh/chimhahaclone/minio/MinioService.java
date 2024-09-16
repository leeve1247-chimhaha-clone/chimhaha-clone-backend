package com.multirkh.chimhahaclone.minio;

import com.multirkh.chimhahaclone.util.IdGenerator;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public void deleteImages(Set<String> fileNames) {
        List<DeleteObject> objects = new LinkedList<>();
        for (String fileName : fileNames) {
            objects.add(new DeleteObject(fileName));
        }
        try{
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket("my-bucketname").objects(objects).build());
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
}
