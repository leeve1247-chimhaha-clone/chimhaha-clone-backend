package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.service.image.ImageService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
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
}
