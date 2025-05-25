package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import lombok.Getter;

import java.time.ZonedDateTime;

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
    private final String userAuthId;

    public PostDetailDto(Post post, JsonNode content){
        this.title = post.getTitle();
        this.username = post.getUser().getUserName();
        this.status = post.getStatus();
        this.createdDate = post.getCreatedDate();
        this.views = post.getViews();
        this.category = post.getCategory().getKey();
        this.likes = post.getLikes();
        this.postId = post.getId().intValue();
        this.content = content;
        this.userAuthId = post.getUser().getUserAuthId();
    }
}