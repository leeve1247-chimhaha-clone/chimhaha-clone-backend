// src/main/java/com/multirkh/chimhahaclone/dto/PostListComponentDto.java
package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostStatus;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class PostDetailDto {
    private final String title;
    private final String username;
    private final PostStatus status;
    private final ZonedDateTime createdDate;
    private final Integer views;
    private final String category;
    private final Integer likes;
    private final Integer postId;
    private final JsonNode content;
    private final List<CommentDto> comments;

    public PostDetailDto(Post post){
        this.title = post.getTitle();
        this.username = post.getUser().getUserName();
        this.status = post.getStatus();
        this.createdDate = post.getCreatedDate();
        this.views = post.getViews();
        this.category = post.getCategory().getName();
        this.likes = post.getLikes();
        this.postId = post.getId().intValue();
        this.content = post.getJsonContent();
        this.comments = post.getComments().stream().filter(comment -> comment.getParent() == null).map(CommentDto::new).toList();
    }
}