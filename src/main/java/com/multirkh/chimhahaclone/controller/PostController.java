package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.redis.ViewCountService;
import com.multirkh.chimhahaclone.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostLikesUserRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import com.multirkh.chimhahaclone.service.ImageService;
import com.multirkh.chimhahaclone.service.PostService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;
    private final ViewCountService viewCountService;
    private final PostLikesUserRepository postLikesUserRepository;
    private final ImageService imageService;
    private final PostService postService;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostListComponentDto> getPosts() {
        return postService.findPostList();
    }

    @GetMapping("/posts/detail")
    public PostDetailDto getPosts(@RequestParam(name = "num", defaultValue = "0") Long listNum) {
        viewCountService.incrementViewCount(listNum);
        return  postService.findPost(listNum);
    }

    @PostMapping("/save")
    @RolesAllowed("USER")
    public String createPost(
            @RequestBody PostReceived request
    ) {
        postService.validateCreatePost(request);
        Post post = postService.createPost(request);
        imageService.createPostImages(post);
        return post.getId().toString();
    }

    @PostMapping("/update")
    @RolesAllowed("USER")
    public String updatePost(
            @RequestBody PostReceived request
    ) {
        Post post = postService.validateUpdatePost(request);
        imageService.updatePostImage(post, request.getContent(), request.getTitleImageFileName());
        return postService.updatePost(post, request);
    }

    @PostMapping("/delete")
    @RolesAllowed("USER")
    public String deletePost(
            @RequestBody PostReceived request
    ) {

        Post post = postService.validateDeletePost(request);
        imageService.deletePostImage(post);
        return postService.deletePost(post);
    }

    @PostMapping("/posts/like")
    @RolesAllowed("USER")
    public Integer likePost(
            @RequestBody Map<String, Long> body
    ) {
        return postService.updateLikesCount(body);
    }
}


