package com.multirkh.chimhahaclone.api.image.dtos;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class PresignedPostDto {
    String url;
    Map<String, String> fields;

    public PresignedPostDto(
            String fileName,
            String url,
            Map<String, String> presignedPost
    ) {
        this.url = url;
        this.fields = new HashMap<>(presignedPost);
        this.fields.put("key", fileName);
    }
}
