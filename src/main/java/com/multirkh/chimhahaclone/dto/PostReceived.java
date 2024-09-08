package com.multirkh.chimhahaclone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReceived {
    @JsonProperty("category")
    private String postCategoryName;
    private JsonNode content;
    private String title;
    private String user;
    @JsonProperty("titleImage")
    private String titleImageFileName;
    @JsonProperty("postId")
    private String postId;
}
