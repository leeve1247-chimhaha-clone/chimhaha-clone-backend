package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategorySearchControllerTest {
    @Autowired
    private PostCategoryRepository postCategoryRepository;
}