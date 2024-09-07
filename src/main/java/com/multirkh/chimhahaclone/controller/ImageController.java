package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.minio.MinioService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final MinioService minioService;

    @RolesAllowed("USER")
    @PostMapping("/upload/image")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String randomImageName = minioService.postFileWithRandomFileName(file);
        return minioService.getPreviewUrl(randomImageName);
    }
}
