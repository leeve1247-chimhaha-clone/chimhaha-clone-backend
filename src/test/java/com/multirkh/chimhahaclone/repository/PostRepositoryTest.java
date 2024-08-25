package com.multirkh.chimhahaclone.repository;

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

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreatePost() {
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("password");

        Post post = new Post(
                "Test Post",
                "This is a test post.",
                "This is a test post.",
                0,
                null,
                user,
                0
        );
        Post savedPost = postRepository.save(post);
        log.info("Saved post: {}", savedPost);
        assertNotNull(savedPost.getId());
    }

    @Test
    void testReadPost() {
        Post post = new Post(
                "Test Post",
                "This is a test post.",
                "This is a test post.",
                0,
                null,
                null,
                0
        );
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
        Post post = new Post(
                "Test Post",
                "This is a test post.",
                "Updated content.",
                0,
                null,
                null,
                0
        );
        Post savedPost = postRepository.save(post);
        savedPost.setStatus(PostStatus.EDITED);
        Post updatedPost = postRepository.save(savedPost);

        Optional<Post> foundPost = postRepository.findById(updatedPost.getId());
        assertTrue(foundPost.isPresent());
        assertEquals("Updated content.", foundPost.get().getContent());
        assertEquals(PostStatus.EDITED, foundPost.get().getStatus());
    }

    @Test
    void testDeletePost() {
        Post post = new Post(
                "Test Post",
                "This is a test post.",
                "This is a test post.",
                0,
                null,
                null,
                0
        );
        Post savedPost = postRepository.save(post);

        postRepository.deleteById(savedPost.getId());
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertFalse(foundPost.isPresent());
    }

    @Test
    void findByUserName(){
        List<User> userA = userRepository.findByEmail("userA@example.com");
        List<Post> posts = postRepository.findByUser(userA.getFirst());
        for(Post post : posts){
            System.out.println("post = " + post.getContent());
        }
    }
}