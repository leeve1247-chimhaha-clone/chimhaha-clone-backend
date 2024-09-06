package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReceived {
    private JsonNode content;
    private Long postId;
    private Long commentId;
}
