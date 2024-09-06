package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.CommentRepository;
import com.multirkh.chimhahaclone.repository.PostCategoryRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @PostMapping("/save/comment")
    public String saveComment(
            @RequestBody CommentReceived request
    ) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(name);
        JsonNode jsonContent = request.getContent();
        if (postRepository.findById(request.getPostId()).isEmpty()) return "There is no post";
        Post post = postRepository.findById(request.getPostId()).get();
        if (request.getCommentId() == null) {
            Comment comment = new Comment(jsonContent, post, user, 0);
            commentRepository.save(comment);
        }
        else {
            Comment parent = commentRepository.findById(request.getCommentId()).orElse(null);
            Comment comment = new Comment(jsonContent, post, user, 0, parent);
            commentRepository.save(comment);
        }
        return "Your comment has saved very well";
    }
}
