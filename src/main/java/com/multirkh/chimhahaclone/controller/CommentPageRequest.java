package com.multirkh.chimhahaclone.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentPageRequest {
    private Long postId;
    private Long pageNum;
    private Long commentId;
}
