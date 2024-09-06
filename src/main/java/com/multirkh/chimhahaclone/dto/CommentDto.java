package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Comment;
import lombok.Getter;

@Getter
public class CommentDto {
    private final String username;
    private final JsonNode content;
    private Long id;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUserName();
        this.content = comment.getContent();
    }
}
