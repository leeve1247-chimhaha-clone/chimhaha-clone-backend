package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.minio.MinioService;
import com.multirkh.chimhahaclone.repository.ImageRepository;
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
    private final ImageRepository imageRepository;

    @RolesAllowed("USER")
    @PostMapping("/upload/image")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        // save into minio
        String randomImageName = minioService.postFileWithRandomFileName(file);
        // save into db
        imageRepository.save(new Image(randomImageName, file.getContentType()));
        return minioService.getPreviewUrl(randomImageName);
    }
}
