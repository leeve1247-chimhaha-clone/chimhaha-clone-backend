package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.dto.PostCategoryDto;
import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostCategoryController {
    private final PostCategoryService postCategoryService;

    @GetMapping("/post-categories")
    public List<PostCategoryDto> getPostCategories() {
        return postCategoryService.findAllTree();
    }

    @GetMapping("/post-categories/flat")
    public List<PostCategoryDto> getPostCategoriesFlat() {
        return postCategoryService.findAllFlat();
    }
}
