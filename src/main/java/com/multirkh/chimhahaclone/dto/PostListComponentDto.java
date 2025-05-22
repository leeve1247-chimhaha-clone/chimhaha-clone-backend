// src/main/java/com/multirkh/chimhahaclone/dto/PostListComponentDto.java
package com.multirkh.chimhahaclone.dto;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class PostListComponentDto {
    private final String title;
    private final String username;
    private final PostStatus status;
    private final ZonedDateTime createdDate;
    private final Integer views;
    private final String category;
    private final Integer likes;
    private final Integer postId;
    private final String titleImageFileName;
    private final Integer commentsCount;

    public PostListComponentDto(Post post){
        this.title = post.getTitle();
        this.username = post.getUser().getUserName();
        this.status = post.getStatus();
        this.createdDate = post.getCreatedDate();
        this.views = post.getViews();
        this.category = post.getCategory().getKey();
        this.likes = post.getLikes();
        this.postId = post.getId().intValue();
        if (post.getThumbNailImage() != null) {
            this.titleImageFileName = post.getThumbNailImage().getRawImage().getFileName();
        } else {
            this.titleImageFileName = null;
        }
        this.commentsCount = post.getCommentsCount();
    }
}