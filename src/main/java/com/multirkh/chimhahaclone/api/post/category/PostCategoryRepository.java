package com.multirkh.chimhahaclone.api.post.category;

import com.multirkh.chimhahaclone.api.post.category.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    PostCategory findByKey(String key);

    List<PostCategory> findByLevelLessThanEqual(Integer level);

    @Query("SELECT pc FROM PostCategory pc Where pc.id = (SELECT MAX(pc2.id) FROM PostCategory pc2 WHERE pc2.key = pc.key)")
    List<PostCategory> findAllFlat();

    @Query("Select pc FROM PostCategory pc Where pc.key = :key and pc.level <= 2")
    List<PostCategory> findPostCategoryByKey(String key);
}