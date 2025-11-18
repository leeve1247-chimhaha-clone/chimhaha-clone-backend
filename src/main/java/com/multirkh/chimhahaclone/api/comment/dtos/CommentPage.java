package com.multirkh.chimhahaclone.api.comment.dtos;

import org.jetbrains.annotations.NotNull;

public record CommentPage(Long startId, Long endId) {
    @NotNull
    @Override
    public String toString() {
        return "CommentPage{" +
                "startId=" + startId +
                ", endId=" + endId +
                '}';
    }
}
