// src/main/java/com/multirkh/chimhahaclone/dto/PostDto.java
package com.multirkh.chimhahaclone.dto;

import com.multirkh.chimhahaclone.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private PostStatus status;
}