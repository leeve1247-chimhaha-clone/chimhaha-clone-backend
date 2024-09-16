package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.config.TestPostRepository;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.minio.MinioConfig;
import com.multirkh.chimhahaclone.service.ImageService;
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
import java.util.Set;

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
    private MinioConfig minioConfig;
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
    @Autowired
    private ImageService imageService;

    @BeforeEach
    void insertData() {
        User user1 = userRepository.save(new User("aaron", "aaaa"));
        User user2 = userRepository.save(new User("bbron", "bbbb"));
        JsonNode jsonNode1 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");
        JsonNode jsonNode2 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 2\\n\"}]}");
        JsonNode jsonNode3 = jsonNodeOf("{\"ops\": [{\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=NrND8bPKSOTPnvTlvpty.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"center\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"center\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=RBbVllH7HRQ3RnF4udR4.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"right\"}}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=3hv7ChhIgRMt1iPk0RKF.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=40rdJ4dWYhxHPsvfSfsm.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=2nMNo2892mYqfRTsE1cK.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://192.168.1.14:1191/api/v1/buckets/my-bucket/objects/download?preview=true&prefix=yw7S9KAS2E1R3CDwpaES.png\"}}, {\"insert\": \"\\n\"}]}");
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");

        Post post1 = postRepository.save(new Post("Test Post 3", jsonNode1, user1, categoryHobby, ""));
        Post post2 = postRepository.save(new Post("Test Post 4", jsonNode1, user1, categoryHobby, ""));
        Post post3 = postRepository.save(new Post("Test Post 5", jsonNode3, user2, categoryHobby, ""));

        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 1\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 2\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 3\\n\"}]}"), post1, user2, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 4\\n\"}]}"), post2, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 5\\n\"}]}"), post2, user2, 0));
        log.info(String.valueOf(System.identityHashCode(post1)));
        em.flush(); // db commit
        em.clear(); // clear entities
        log.info("===============End Before Init=================");
    }

    @Test
    void testCreatePost() {
        User user = userRepository.save(new User("ccron", "cccc"));
        String title = "Test Post3";
        JsonNode jsonContent = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 3\\n\"}]}");
        ;
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");
        Post post = postRepository.save(new Post(title, jsonContent, user, categoryHobby, ""));
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
    public void testGetPostDetail(){
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        PostDetailDto postDetailDto = new PostDetailDto(post);
        for (CommentDto comment : postDetailDto.getComments()){
            log.info(String.valueOf(comment.getContent()));
        }
    }

    @Test
    public void testGetPostDetail2(){
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        PostDetailDto postDetailDto = new PostDetailDto(post);
        JsonNode content = postDetailDto.getContent();
        JsonNode ops = content.get("ops");
        log.info(String.valueOf(content));
        log.info(String.valueOf(ops));
    }

    @Test
    public void getImages(){
        Post post = postRepository.findByTitleEquals("Test Post 5").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Set<String> images = imageService.getImageUrls(post.getJsonContent());
        for(String image : images){
            log.info(image);
        }
    }


    @Test
    @Rollback(false)
    public void saveTestedDate(){}
}