package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedDateDesc();
    @Query("SELECT p FROM Post p WHERE p.category.key = :category ORDER BY p.createdDate DESC")
    List<Post> findAllByOrderByCreatedDateWhere(String category);
    Optional<Post> findByTitleEquals(String title);
}
