package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostLikesUser;
import com.multirkh.chimhahaclone.entity.PostStatus;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.redis.ViewCountService;
import com.multirkh.chimhahaclone.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostLikesUserRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import com.multirkh.chimhahaclone.service.ImageService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;
    private final ViewCountService viewCountService;
    private final PostLikesUserRepository postLikesUserRepository;
    private final ImageService imageService;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostListComponentDto> getPosts() {
        return postRepository
                .findAllByOrderByCreatedDateDesc()
                .stream()
                .filter(post -> post
                        .getStatus() == PostStatus.POSTED
                )
                .map(PostListComponentDto::new)
                .toList();
    }

    @GetMapping("/posts/detail")
    public PostDetailDto getPosts(@RequestParam(name = "num", defaultValue = "0") Long listNum) {
        viewCountService.incrementViewCount(listNum);
        Post postEntity = postRepository.findById(listNum).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (postEntity.getStatus() == PostStatus.DELETED) {
            return null;
        } else {
            return new PostDetailDto(postEntity);
        }
    }


    @PostMapping("/save")
    @RolesAllowed("USER")
    public String savePost(
            @RequestBody PostReceived request
    ) {
        PostCategory postCategory = postCategoryRepository.findByName(request.getPostCategoryName());
        JsonNode jsonContent = request.getContent();
        String userAuthId = request.getUser();
        String titleImageFileName = request.getTitleImageFileName();
        String title = request.getTitle();
        if (title == null) {
            throw new IllegalArgumentException("title is null");
        }
        User user = userRepository.findByUserAuthId(userAuthId);
        Post post = new Post(title, jsonContent, user, postCategory, titleImageFileName);
        postRepository.save(post);
        imageService.createPostImages(post, jsonContent);
        return post.getId().toString();
    }

    @PostMapping("/update")
    @RolesAllowed("USER")
    public String updatePost(
            @RequestBody PostReceived request
    ) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));

        if (post.getUser().getUserAuthId().equals(user_auth_id)) {
            if (request.getTitle() == null) {
                throw new IllegalArgumentException("title is null");
            }
            post.setTitle(request.getTitle());
            imageService.updatePostImage(post, request.getContent());
            post.setJsonContent(request.getContent());
            post.setCategory(postCategoryRepository.findByName(request.getPostCategoryName()));
            post.setTitleImageFileName(request.getTitleImageFileName());
            Post savedPost = postRepository.save(post);
            return savedPost.getId().toString();
        } else {
            throw new IllegalArgumentException("user is not matched");
        }
    }

    @PostMapping("/delete")
    @RolesAllowed("USER")
    public String deletePost(
            @RequestBody PostReceived request
    ) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));

        if (post.getUser().getUserAuthId().equals(user_auth_id)) {
            post.setJsonContent(null);
            post.setStatus(PostStatus.DELETED);
            Post savedPost = postRepository.save(post);
            return savedPost.getId().toString();
        } else {
            throw new IllegalArgumentException("user is not matched");
        }
    }

    @GetMapping("/posts/like")
    @RolesAllowed("USER")
    public String likePost(
            @RequestParam(name = "num", defaultValue = "0") Long listNum
    ) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(listNum).orElseThrow(() -> new IllegalArgumentException("post not found"));
        User user = userRepository.findByUserAuthId(user_auth_id);
        PostLikesUser postLikesUser = postLikesUserRepository.findByPostAndUser(post, user);
        if (postLikesUser == null) {
            post.setLikes(post.getLikes() + 1);
            postLikesUser = new PostLikesUser(post, user);
            postLikesUserRepository.save(postLikesUser);
            postRepository.save(post);
            return "liked";
        } else {
            post.setLikes(post.getLikes() - 1);
            postLikesUserRepository.delete(postLikesUser);
            postRepository.save(post);
            return "like canceled";
        }
    }
}


