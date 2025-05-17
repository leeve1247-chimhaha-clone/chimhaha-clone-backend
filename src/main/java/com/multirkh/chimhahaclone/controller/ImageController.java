package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.service.image.ImageService;
import com.multirkh.chimhahaclone.service.image.PresignedUrlDTO;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @RolesAllowed("USER")
    @PostMapping("/upload/image")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        imageService.validateImage(file);
        return imageService.createImage(file);
    }

    @GetMapping("/get/presigned-url")
    public PresignedUrlDTO getPresignedUrl() {
        return imageService.getPresignedUrl();
    }

    @GetMapping("/get/presigned-url2")
    public String getPresignedUrl2(@RequestParam("filename") String fileName ){
        return imageService.getPresignedUrl2(fileName);
    }
}
