package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<PostDto> getPosts(@RequestParam(name = "list_num", required = false, defaultValue = "0") int listNum) {
        return postRepository.findAllByOrderByCreatedDateDesc().stream().map(PostDto::new).toList();
    }

    @PostMapping("/save")
    public String savePost(
            @RequestBody PostReceived request
    ) {
        System.out.println("request = " + request);
        System.out.println("request = " + request.getPostCategoryName());
        PostCategory postCategory = postCategoryRepository.findByName(request.getPostCategoryName());
        JsonNode jsonContent = request.getContent();
        String userAuthId = request.getUser();
        User user = userRepository.findByUserAuthId(userAuthId);
        if(user == null){
            user = new User(userAuthId);
            userRepository.save(user);
        }
        System.out.println("jsonContent = " + jsonContent);
        String title = request.getTitle();
        Post post = new Post(title, jsonContent, user, postCategory);
        Post save = postRepository.save(post);
        return "Post saved!";
    }
}
