package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.category.repository.CategoryRepository;
import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String savePost(@RequestParam(name = "title") String title,
                         @RequestParam(name = "body") String body,
                         @RequestParam(name = "content") String content,
                         @RequestParam(name = "category") String category,
                         @RequestParam(name = "user") Long userId) {
        PostCategory requestedCategory = categoryRepository.findByName(category);
        User requestedUser = userRepository.findById(userId).orElseThrow();
        Post newPost = new Post(
                title,
                body,
                content,
                0,
                requestedCategory,
                requestedUser,
                0
        );
        postRepository.save(newPost);

        return "Post saved!";
    }
}
