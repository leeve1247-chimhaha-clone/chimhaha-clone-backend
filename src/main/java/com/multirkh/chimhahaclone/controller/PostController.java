package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    @GetMapping("/")
    public String home() {
        return "Hello, Chimhaha Clone!";
    }

    @GetMapping("/posts")
    public List<PostDto> getPosts(@RequestParam(name = "list_num", required = false, defaultValue = "0") int listNum) {
        return postRepository.findAll().stream().map(post -> new PostDto(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUsername(),
                post.getStatus())).toList();
    }
}
