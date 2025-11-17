package com.multirkh.chimhahaclone.api.comment.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class CategorySearchResultDto {
    private final List<String> categoryNameList = new ArrayList<>();

    public CategorySearchResultDto() {

    }
}
