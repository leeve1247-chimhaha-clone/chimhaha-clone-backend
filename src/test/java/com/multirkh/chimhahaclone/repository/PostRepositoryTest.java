package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Import({JwtDecoderTestConfig.class, DataInitializer.class})
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Test
    void testCreatePost() {
        User user = new User();
        user.setUserName("aaron");
        user.setUserAuthId("aabbccdd");

        String title = "Test Post";
        JsonNode jsonContent = null;
        PostCategory postCategory = postCategoryRepository.findByName("HOBBY");

        Post post = new Post(title, jsonContent, user, postCategory);
        postRepository.save(post);
        userRepository.save(user);
        }
    }