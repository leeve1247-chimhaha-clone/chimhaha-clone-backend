package com.multirkh.chimhahaclone.api.image;

import com.multirkh.chimhahaclone.api.image.dtos.PresignedUrlDTO;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @RolesAllowed("USER")
    @GetMapping("/get/presigned-url")
    public PresignedUrlDTO getPresignedUrl() {
        return imageService.getPresignedUrl();
    }

    @GetMapping("/get/src-url")
    public String getSrcUrl(@RequestParam("filename") String fileName ){
        return imageService.getSrcUrl(fileName);
    }

    @GetMapping("/get/thumbnail-src-url")
    public String getThumbnailSrcUrl(@RequestParam("filename") String fileName ) {
        return imageService.getThumbnailSrcUrl(fileName);
    }
}
