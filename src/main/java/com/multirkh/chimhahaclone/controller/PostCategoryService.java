package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.dto.PostCategoryDto;
import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.service.comment.CommentPage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public List<PostCategoryDto> findAllTree() {
        List<PostCategory> postCategories = postCategoryRepository.findByLevelLessThanEqual(2);
        List<PostCategoryDto> postCategoryDtoList = postCategories.stream().map(PostCategoryDto::new).toList();
        Map<Long, PostCategoryDto> postCategoryDtoMap = postCategoryDtoList.stream().collect(Collectors.toMap(PostCategoryDto::getId, c -> c));
        for (PostCategoryDto postCategoryDto : postCategoryDtoList) {
            if (postCategoryDto.getParentId() != null) {
                postCategoryDtoMap.get(postCategoryDto.getParentId()).getChildren().add(postCategoryDto);
            }
        }
        return postCategoryDtoList.stream().filter(pdt -> pdt.getParentId() == null).collect(Collectors.toList());
    }

    public List<PostCategoryDto> findAllFlat() {
        List<PostCategory> postCategoryList = postCategoryRepository.findAllFlat();
        return postCategoryList.stream().map(PostCategoryDto::new
        ).toList();
    }
}
