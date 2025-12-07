package com.multirkh.chimhahaclone.api.image.dtos;

import java.util.Map;
import lombok.Getter;

@Getter
public class PresignedPostDto {
    String url;
    Map<String, String> fields;

    public PresignedPostDto(Map<String, String> presignedPost, String fileName, String url) {
        this.url = url;
        this.fields = presignedPost;
        this.fields.put("key", fileName);
    }
}
