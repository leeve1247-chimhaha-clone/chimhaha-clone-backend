package com.multirkh.chimhahaclone.api.comment.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentPage {
    private final Long startId;
    private final Long endId;

    @Override
    public String toString(){
        return "CommentPage{" +
                "startId=" + startId +
                ", endId=" + endId +
                '}';
    }
}
