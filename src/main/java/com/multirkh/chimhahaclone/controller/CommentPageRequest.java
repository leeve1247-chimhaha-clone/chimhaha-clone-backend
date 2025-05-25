package com.multirkh.chimhahaclone.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentPageRequest {
    private Long postId;
    private Long pageNum;
    private Long commentId;
}
