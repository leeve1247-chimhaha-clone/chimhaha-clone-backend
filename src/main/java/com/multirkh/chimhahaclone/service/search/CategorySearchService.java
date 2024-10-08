package com.multirkh.chimhahaclone.service.search;

import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.dto.CategorySearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategorySearchService {
    private final PostCategoryRepository postCategoryRepository;

    public CategorySearchResultDto getSearchResult(String searchTerm) {
        CategorySearchResultDto categorySearchResultDto = new CategorySearchResultDto();
        categorySearchResultDto.getCategoryNameList().add(searchTerm);
        return categorySearchResultDto;
    }
}
