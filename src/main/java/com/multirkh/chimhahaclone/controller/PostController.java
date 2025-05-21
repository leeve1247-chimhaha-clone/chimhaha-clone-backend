package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.redis.ViewCountService;
import com.multirkh.chimhahaclone.category.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostLikesUserRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import com.multirkh.chimhahaclone.service.image.ImageService;
import com.multirkh.chimhahaclone.service.post.PostService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
    public List<PostListComponentDto> getPosts(@RequestParam(name="category", required = false) String category) {
        if (category == null){return postService.findPostList();}
        return postService.findPostList(category);
    }

    @GetMapping("/posts/detail")
    public PostDetailDto getPosts(@RequestParam(name = "num") Long postNum) {
        viewCountService.incrementViewCount(postNum);
        return  postService.findPost(postNum);
    }

    @PostMapping("/save")
    public String createPost(
            @RequestBody PostReceived request
    ) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println("Authority: " + authority.getAuthority());
        }
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


