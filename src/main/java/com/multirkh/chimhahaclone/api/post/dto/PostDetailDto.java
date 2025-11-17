package com.multirkh.chimhahaclone.api.post.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.domain.PostStatus;
import java.time.ZonedDateTime;
import lombok.Getter;

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
    private final Boolean selfLiked;

    public PostDetailDto(Post post, JsonNode content, Boolean selfLiked) {
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
        this.selfLiked = selfLiked;
    }
}