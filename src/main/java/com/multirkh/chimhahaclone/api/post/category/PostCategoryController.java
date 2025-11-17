package com.multirkh.chimhahaclone.api.post.category;

import com.multirkh.chimhahaclone.api.post.category.dto.PostCategoryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
