package com.multirkh.chimhahaclone.api.comment.dtos;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategorySearchResultDto {
    private final List<String> categoryNameList = new ArrayList<>();

    public CategorySearchResultDto() {

    }
}
