package com.multirkh.chimhahaclone.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostStatus;
import com.multirkh.chimhahaclone.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void testCreatePost() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setStatus(PostStatus.POSTED);
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        log.info("Saved post: {}", savedPost);
        assertNotNull(savedPost.getId());
    }

    @Test
    void testReadPost() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setStatus(PostStatus.POSTED);
        Post savedPost = postRepository.save(post);

        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertTrue(foundPost.isPresent());
        assertEquals("Test Post", foundPost.get().getTitle());
    }

    @Test
    void findAllTest(){
        List<PostDto> list = postRepository.findAll().stream().map(PostDto::new).toList();
        System.out.println("all = " + list);
    }

    @Test
    void testUpdatePost() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setStatus(PostStatus.POSTED);
        Post savedPost = postRepository.save(post);

        savedPost.setContent("Updated content.");
        savedPost.setStatus(PostStatus.EDITED);
        Post updatedPost = postRepository.save(savedPost);

        Optional<Post> foundPost = postRepository.findById(updatedPost.getId());
        assertTrue(foundPost.isPresent());
        assertEquals("Updated content.", foundPost.get().getContent());
        assertEquals(PostStatus.EDITED, foundPost.get().getStatus());
    }

    @Test
    void testDeletePost() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setStatus(PostStatus.POSTED);
        Post savedPost = postRepository.save(post);

        postRepository.deleteById(savedPost.getId());
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertFalse(foundPost.isPresent());
    }
}