package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Comment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentDto {
    private final String username;
    private final JsonNode content;
    private final Long id;
    private final Integer likes;
    private final List<CommentDto> children = new ArrayList<>();

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUserName();
        this.content = comment.getContent();
        this.likes = comment.getLikes();
        if (comment.getChildren() != null) {
            comment.getChildren().forEach(child -> children.add(new CommentDto(child)));
        }
    }
}
