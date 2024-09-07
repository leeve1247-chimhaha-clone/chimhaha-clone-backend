package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostStatus;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostListComponentDto> getPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc().stream().map(PostListComponentDto::new).toList();
    }

    @GetMapping("/posts/detail")
    public PostDetailDto getPosts(@RequestParam(name = "num", defaultValue = "0") Long listNum) {
        Optional<Post> optionalPostEntity = postRepository.findById(listNum);
        if (optionalPostEntity.isEmpty()) {
            return null;
        } else {
            Post postEntity = optionalPostEntity.get();
            if (postEntity.getStatus() == PostStatus.DELETED) {
                return null;
            } else {
                return optionalPostEntity.stream().map(PostDetailDto::new).findFirst().get();
            }
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
        return post.getId().toString();
    }
}
