package com.multirkh.chimhahaclone.api.post;

import com.multirkh.chimhahaclone.api.post.likes.dto.LikeRequest;
import com.multirkh.chimhahaclone.api.post.dto.PostDetailDto;
import com.multirkh.chimhahaclone.api.post.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.api.post.dto.PostReceived;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.common.redis.ViewCountService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final ViewCountService viewCountService;
    private final PostService postService;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostListComponentDto> getPosts(@RequestParam(name="category", required = false) String category) {
        if (category == null || category.equalsIgnoreCase("all")){return postService.findPostList();}
        return postService.findPostList(category);
    }

    @GetMapping("/posts/detail")
    public PostDetailDto getPosts(@RequestParam(name = "num") Long postNum) {
        viewCountService.incrementViewCount(postNum);
        return postService.findPost(postNum);
    }

    @PostMapping("/save")
    public String createPost(
            @RequestBody PostReceived request
    ) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        postService.validateCreatePost(request);
        return postService.createPost(request);
    }

    @PostMapping("/update")
    @RolesAllowed("USER")
    public String updatePost(
            @RequestBody PostReceived request
    ) {
        postService.validateUpdatePost(request);
        return postService.updatePost(request);
    }

    @PostMapping("/delete")
    @RolesAllowed("USER")
    public String deletePost(
            @RequestBody PostReceived request
    ) {
        Post post = postService.validateDeletePost(request);
        return postService.deletePost(post);
    }

    @PostMapping("/posts/like")
    @RolesAllowed("USER")
    public Integer likePost(
            @RequestBody LikeRequest request
    ) {
        return postService.updateLikesCount(request);
    }
}


