package com.multirkh.chimhahaclone.service.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PresignedUrlDTO {
    String url;
    @JsonProperty("filename")
    String fileName;
    public PresignedUrlDTO(String presignedUrl, String fileName) {
        this.url = presignedUrl;
        this.fileName = fileName;
    }
}
