package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentDto {
    private final String username;
    private final JsonNode content;
    private final Long id;
    private final Integer likes;
    private final List<CommentDto> children = new ArrayList<>();
    private final PostStatus status;
    private final ZonedDateTime lastEditedDate;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUserName();
        this.content = comment.getContent();
        this.likes = comment.getLikes();
        this.status = comment.getStatus();
        this.lastEditedDate = comment.getEditedDate();
        if (comment.getChildren() != null) {
            comment.getChildren().forEach(child -> children.add(new CommentDto(child)));
        }
    }
}
