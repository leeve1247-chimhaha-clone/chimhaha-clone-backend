package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReceived {
    private JsonNode content;
    private Long postId;
    @JsonProperty("commentId")
    private Long parentCommentId;
}
