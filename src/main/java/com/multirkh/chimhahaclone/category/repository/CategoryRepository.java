package com.multirkh.chimhahaclone.category.repository;

import com.multirkh.chimhahaclone.category.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<PostCategory, Long> {

    PostCategory findByName(String name);
}