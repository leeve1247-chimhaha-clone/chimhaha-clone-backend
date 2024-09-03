package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.category.repository.CategoryRepository;
import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostDto> getPosts(@RequestParam(name = "list_num", required = false, defaultValue = "0") int listNum) {
        return postRepository.findAll().stream().map(PostDto::new).toList();
    }

    @PostMapping("/save")
    public String savePost(
            @RequestBody PostReceived request
    ) {
        PostCategory postCategory = request.getPostCategory();
        return "Post saved!";
    }
}
