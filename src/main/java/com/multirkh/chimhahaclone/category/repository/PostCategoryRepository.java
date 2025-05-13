package com.multirkh.chimhahaclone.category.repository;

import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    PostCategory findByKey(String key);

    List<PostCategory> findPostCategoriesByParent (PostCategory postCategory);

}