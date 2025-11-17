package com.multirkh.chimhahaclone.api.comment.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public record CommentPage(Long startId, Long endId) {
    @Override
    public String toString() {
        return "CommentPage{" +
                "startId=" + startId +
                ", endId=" + endId +
                '}';
    }
}
