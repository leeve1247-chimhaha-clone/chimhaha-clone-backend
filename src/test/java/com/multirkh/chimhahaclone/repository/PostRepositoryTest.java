package com.multirkh.chimhahaclone.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multirkh.chimhahaclone.bootup.DataInitializer;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.config.JwtDecoderTestConfig;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.entity.*;
import com.multirkh.chimhahaclone.minio.MinioConfig;
import com.multirkh.chimhahaclone.minio.MinioService;
import com.multirkh.chimhahaclone.minio.TestMinioService;
import com.multirkh.chimhahaclone.service.ImageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TestMinioService minioService;

    @BeforeEach
    void insertData() {
        imageRepository.save(new Image("1.png", "image/png"));
        imageRepository.save(new Image("2.png", "image/png"));
        imageRepository.save(new Image("3.png", "image/png"));
        imageRepository.save(new Image("4.png", "image/png"));
        imageRepository.save(new Image("5.png", "image/png"));
        imageRepository.save(new Image("6.png", "image/png"));
        imageRepository.save(new Image("7.png", "image/png"));
        imageRepository.save(new Image("8.png", "image/png"));

        minioService.postMockFile("1.png");
        minioService.postMockFile("2.png");
        minioService.postMockFile("3.png");
        minioService.postMockFile("4.png");
        minioService.postMockFile("5.png");
        minioService.postMockFile("6.png");
        minioService.postMockFile("7.png");
        minioService.postMockFile("8.png");

        User user1 = userRepository.save(new User("aaron", "aaaa"));
        User user2 = userRepository.save(new User("bbron", "bbbb"));
        JsonNode jsonNode1 = jsonNodeOf("{\"ops\": [{\"insert\": \"post sample 1\\n\"}]}");
        JsonNode jsonNode2 = jsonNodeOf("{\"ops\": [{\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=6.png\"}}, {\"insert\": \"\\n\"}]}");
        JsonNode jsonNode3 = jsonNodeOf("{\"ops\": [{\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=1.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"center\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"center\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=2.png\"}}, {\"insert\": \"\\n\\n\", \"attributes\": {\"align\": \"right\"}}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=3.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=4.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=5.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=6.png\"}}, {\"insert\": \"\\n\"}]}");
        PostCategory categoryHobby = postCategoryRepository.findByName("HOBBY");

        Post post1 = postRepository.save(new Post("Test Post 1", jsonNode1, user1, categoryHobby, "6.png"));
        Post post2 = postRepository.save(new Post("Test Post 2", jsonNode1, user1, categoryHobby, "1.png"));
        Post post3 = postRepository.save(new Post("Test Post 3", jsonNode3, user2, categoryHobby, ""));

        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 1\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 2\\n\"}]}"), post1, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 3\\n\"}]}"), post1, user2, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 4\\n\"}]}"), post2, user1, 0));
        commentRepository.save(new Comment(jsonNodeOf("{\"ops\": [{\"insert\": \"comment sample 5\\n\"}]}"), post2, user2, 0));

        imageService.createPostImages(post2, jsonNode2, "6.png");
        imageService.createPostImages(post3, jsonNode3, "1.png");
        
        log.info(String.valueOf(System.identityHashCode(post1)));
        em.flush(); // db commit
        em.clear(); // clear entities
        log.info("post3 size is {}",post3.getPostImages().size());
        log.info("===============End Before Init=================");
    }

    @Test
    @Order(1)
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
    @Order(1)
    void loadPostDetailDto() {
        List<Comment> commentList = commentRepository.findAll();
        Post post = postRepository.findByTitleEquals("Test Post 1").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        for (Comment comment : commentList) {
            log.info(String.valueOf(comment.getContent()));
        }
    }

    @Test
    @Order(1)
    public void testGetPostWithComments() {
        // BeforeEach 의 post 와 동일한 post 를 참조함
        Post post = postRepository.findByTitleEquals("Test Post 1").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        log.info(String.valueOf(System.identityHashCode(post)));
        em.clear();
        post = postRepository.findByTitleEquals("Test Post 1").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        log.info(String.valueOf(System.identityHashCode(post)));
        log.info(String.valueOf(post.getComments().size()));
        assertThat(post.getComments()).isNotNull();
        assertThat(post.getComments().size()).isGreaterThan(0);
    }

    @Test
    @Order(1)
    public void testGetPostDetail(){
        Post post = postRepository.findByTitleEquals("Test Post 1").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        PostDetailDto postDetailDto = new PostDetailDto(post);
        for (CommentDto comment : postDetailDto.getComments()){
            log.info(String.valueOf(comment.getContent()));
        }
    }

    @Test
    @Order(1)
    public void testGetPostDetail2(){
        Post post = postRepository.findByTitleEquals("Test Post 1").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        PostDetailDto postDetailDto = new PostDetailDto(post);
        JsonNode content = postDetailDto.getContent();
        JsonNode ops = content.get("ops");
        log.info(String.valueOf(content));
        log.info(String.valueOf(ops));
    }

    @Test
    @Order(1)
    public void getImages(){
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Set<PostImage> postImages = post.getPostImages();
        log.info("PostImages size : " + postImages.size());
    }

    @Test
    @Order(1)
    public void updateImages(){
        JsonNode jsonNode = jsonNodeOf("{\"ops\": [{\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=7.png\"}},{\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=8.png\"}}, {\"insert\": \"\\n\"}, {\"insert\": {\"image\": \"http://testtest/aas?preview=true&prefix=4.png\"}}]}");
        Post post = postRepository.findByTitleEquals("Test Post 3").orElseThrow(() -> new EntityNotFoundException("Post not found"));
        imageService.updatePostImage(post,jsonNode);
        post.setJsonContent(jsonNode);
        Set<PostImage> postImages = post.getPostImages();
        for (PostImage postImage : postImages){
            log.info("post's image is {}, and the status is {}", postImage.getImage().getFileName(), postImage.getStatus());
        }

        imageRepository.findAll().forEach(image -> {
            log.info("this is {}",image.getFileName());
        });
    }


    @Test
    @Rollback(false)
    @Order(2)
    public void saveTestedDate(){}
}