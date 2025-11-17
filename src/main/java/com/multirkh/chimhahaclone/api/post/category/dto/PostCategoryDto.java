package com.multirkh.chimhahaclone.api.post.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.multirkh.chimhahaclone.api.post.category.domain.PostCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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
