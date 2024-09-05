package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.entity.Comment;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void insertData(){

        User user1 = userRepository.save(new User("aaron", "aaaa"));
        User user2 = userRepository.save(new User("bbron", "bbbb"));
        JsonNode jsonNode1 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");
        JsonNode jsonNode2 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 2\\n\"}]}");
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");

        Post post1 = postRepository.save(new Post("Test Post 1", jsonNode1, user1, categoryHobby));
        Post post2 = postRepository.save(new Post("Test Post 2", jsonNode2, user2, categoryHobby));

        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 1\\n\"}]}"), post1, user1,0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 2\\n\"}]}"), post1, user1,0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 3\\n\"}]}"), post1, user2,0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 4\\n\"}]}"), post2, user1,0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 5\\n\"}]}"), post2, user2,0));
    }

    @Test
    void testCreatePost() {
        User user = userRepository.save(new User("ccron", "cccc"));
        String title = "Test Post3";
        JsonNode jsonContent = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 3\\n\"}]}");;
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");
        Post post = postRepository.save(new Post(title, jsonContent, user, categoryHobby));
        assertEquals(title, post.getTitle());
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