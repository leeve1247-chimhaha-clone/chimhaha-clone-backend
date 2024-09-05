package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static com.multirkh.chimhahaclone.util.UtilStringJsonConverter.jsonNodeOf;

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

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void insertData(){
        userRepository.save(new User("aaron", "aaaa"));
        userRepository.save(new User("bbron", "bbbb"));

        JsonNode jsonNode1 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");
        JsonNode jsonNode2 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 2\\n\"}]}");
        postRepository.save(new Post("Test Post 1", jsonNode1, userRepository.findByUserAuthId("aaron"), postCategoryRepository.findByName("HOBBY")));
        postRepository.save(new Post("Test Post 2", jsonNode2, userRepository.findByUserAuthId("bbron"), postCategoryRepository.findByName("HOBBY")));
    }

    @Test
    void testCreatePost() {
        User user = new User();
        user.setUserName("aaron");
        user.setUserAuthId("aabbccdd");

        String title = "Test Post";
        JsonNode jsonContent = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");;
        PostCategory postCategory = postCategoryRepository.findByName("HOBBY");

        Post post = new Post(title, jsonContent, user, postCategory);
        postRepository.save(post);
        userRepository.save(user);
    }


    @Test
    void loadPostDto() {
        Post post = postRepository.findById(1L).orElseThrow();

        log.info("====================================");
        log.info(String.valueOf(post.getJsonContent()));
        log.info("====================================");
    }

    @Test
    void loadPostDetailDto(){

    }
}