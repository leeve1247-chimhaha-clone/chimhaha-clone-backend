package com.multirkh.chimhahaclone.service.image;

import lombok.Getter;

@Getter
public class PresignedUrlDTO {
    String url;
    String fileName;
    public PresignedUrlDTO(String presignedUrl, String fileName) {
        this.url = presignedUrl;
        this.fileName = fileName;
    }
}
