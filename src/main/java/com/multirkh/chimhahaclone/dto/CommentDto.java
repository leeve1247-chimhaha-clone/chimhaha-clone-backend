package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Comment;

import java.util.Optional;

public class CommentDto {
    private String username;
    private Optional<CommentDto> parentComment;
    private Optional<PostListComponentDto> post;
    private JsonNode content;

    public CommentDto(Comment comment) {
        this.username = comment.getUser().getUserName();
        this.content = comment.getContent();
    }
}
