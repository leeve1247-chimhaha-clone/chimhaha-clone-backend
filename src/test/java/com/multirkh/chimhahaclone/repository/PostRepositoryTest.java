package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.config.TestPostRepository;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.multirkh.chimhahaclone.util.UtilStringJsonConverter.jsonNodeOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@Import({JwtDecoderTestConfig.class, DataInitializer.class})
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostRepositoryTest {
    @Autowired
    private TestPostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager em;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void insertData() {
        User user1 = userRepository.save(new User("aaron", "aaaa"));
        User user2 = userRepository.save(new User("bbron", "bbbb"));
        JsonNode jsonNode1 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");
        JsonNode jsonNode2 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 2\\n\"}]}");
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");

        Post post1 = postRepository.save(new Post("Test Post 3", jsonNode1, user1, categoryHobby));
        Post post2 = postRepository.save(new Post("Test Post 4", jsonNode2, user2, categoryHobby));

        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 1\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 2\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 3\\n\"}]}"), post1, user2, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 4\\n\"}]}"), post2, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 5\\n\"}]}"), post2, user2, 0));
        log.info(String.valueOf(System.identityHashCode(post1)));
        em.flush();
    }

    @Test
    void testCreatePost() {
        User user = userRepository.save(new User("ccron", "cccc"));
        String title = "Test Post3";
        JsonNode jsonContent = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 3\\n\"}]}");
        ;
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");
        Post post = postRepository.save(new Post(title, jsonContent, user, categoryHobby));
        assertEquals(title, post.getTitle());
    }

    @Test
    void loadPostDto() {
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow();
        log.info(String.valueOf(post.getJsonContent()));
    }

    @Test
    void loadPostDetailDto() {
        List<Comment> commentList = commentRepository.findAll();
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        for (Comment comment : commentList) {
            log.info(String.valueOf(comment.getContent()));
        }
    }

    @Test
    public void testGetPostWithComments() {
        // BeforeEach 의 post 와 동일한 post 를 참조함
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        log.info(String.valueOf(System.identityHashCode(post)));
        em.clear();
        post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        log.info(String.valueOf(System.identityHashCode(post)));
        log.info(String.valueOf(post.getComments().size()));
        assertThat(post.getComments()).isNotNull();
        assertThat(post.getComments().size()).isGreaterThan(0);
    }

    @Test
    @Rollback(false)
    public void saveTestedDate(){}
}