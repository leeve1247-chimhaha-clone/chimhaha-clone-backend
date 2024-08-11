// src/main/java/com/multirkh/chimhahaclone/dto/PostDto.java
package com.multirkh.chimhahaclone.dto;

import com.multirkh.chimhahaclone.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDto {
    private String title;
    private String username;
    private PostStatus status;
}