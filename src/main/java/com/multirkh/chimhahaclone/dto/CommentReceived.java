package com.multirkh.chimhahaclone.dto;

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
