package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedDateDesc();

}
