package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.category.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    PostCategory findByName(String categoryName);

}
