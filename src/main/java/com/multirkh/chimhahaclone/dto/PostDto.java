// src/main/java/com/multirkh/chimhahaclone/dto/PostDto.java
package com.multirkh.chimhahaclone.dto;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class PostDto {
    private String title;
    private String username;
    private PostStatus status;
    private Date createdDate;

    public PostDto(Post post){
        this.title = post.getTitle();
        this.username = post.getUser().getUsername();
        this.status = post.getStatus();
        this.createdDate = post.getCreatedDate();
    }
}