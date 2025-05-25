package com.multirkh.chimhahaclone.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostCategoryDto {
    private final Long id;
    private final Integer level;
    private final String key;
    private final String korean;
    private final List<PostCategoryDto> children = new ArrayList<>();
    @JsonIgnore
    private final Long parentId;

    public PostCategoryDto(PostCategory postCategory) {
        this.id = postCategory.getId();
        this.level = postCategory.getLevel();
        this.key = postCategory.getKey();
        this.korean = postCategory.getKorean();
        if (postCategory.getParent() != null) {
            this.parentId = postCategory.getParent().getId();
            return;
        }
        this.parentId = null;
    }
}
