package com.multirkh.chimhahaclone.service.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
