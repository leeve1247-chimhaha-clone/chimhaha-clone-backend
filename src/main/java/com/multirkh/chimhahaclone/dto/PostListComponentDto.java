// src/main/java/com/multirkh/chimhahaclone/dto/PostListComponentDto.java
package com.multirkh.chimhahaclone.dto;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PostListComponentDto {
    private String title;
    private String username;
    private PostStatus status;
    private ZonedDateTime createdDate;
    private Integer views;
    private String category;
    private Integer likes;
    private Integer postId;

    public PostListComponentDto(Post post){
        this.title = post.getTitle();
        this.username = post.getUser().getUserName();
        this.status = post.getStatus();
        this.createdDate = post.getCreatedDate();
        this.views = post.getViews();
        this.category = post.getCategory().getName();
        this.likes = post.getLikes();
        this.postId = post.getId().intValue();
    }
}