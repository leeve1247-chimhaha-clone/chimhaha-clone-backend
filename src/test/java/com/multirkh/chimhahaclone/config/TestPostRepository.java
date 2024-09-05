package com.multirkh.chimhahaclone.config;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.repository.PostRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestPostRepository extends PostRepository {
    Optional<Post> findByTitleEquals(String title);
}
