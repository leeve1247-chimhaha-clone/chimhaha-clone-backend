package com.multirkh.chimhahaclone.api.comment.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentPageRequest {
    private Long postId;
    private Long pageNum;
    private Long commentId;
}
