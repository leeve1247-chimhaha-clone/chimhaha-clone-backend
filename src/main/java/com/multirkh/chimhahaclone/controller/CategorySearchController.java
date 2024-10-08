package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.CategorySearchResultDto;
import com.multirkh.chimhahaclone.service.search.CategorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategorySearchController {

    private final CategorySearchService categorySearchService;

    @GetMapping
    public CategorySearchResultDto searchCategory(
            @RequestParam(name = "num", defaultValue = "0") String searchTerm) {
            return  categorySearchService.getSearchResult(searchTerm);
    }

}
