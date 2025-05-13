package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.dto.PostCategoryDto;
import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public List<PostCategoryDto> findAllTree() {
        List<PostCategory> postCategoryList = postCategoryRepository.findPostCategoriesByParent(null);
        return postCategoryList.stream().filter(postCategory -> postCategory.getParent() == null)
                .map(PostCategoryDto::new)
                .toList();
    }
}
