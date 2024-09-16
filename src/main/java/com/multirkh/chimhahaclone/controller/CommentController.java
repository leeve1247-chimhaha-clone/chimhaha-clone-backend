package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.entity.*;
import com.multirkh.chimhahaclone.repository.*;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesUserRepository commentLikesUserRepository;

    @PostMapping("/save/comment")
    @RolesAllowed("USER")
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
        } else {
            Comment parent = commentRepository.findById(request.getCommentId()).orElse(null);
            Comment comment = new Comment(jsonContent, post, user, 0, parent);
            commentRepository.save(comment);
        }
        return "Your comment has saved very well";
    }

    @GetMapping("/comments/like")
    @RolesAllowed("USER")
    public String likeComment(
            @RequestParam(name = "num", defaultValue = "0") Long listNum
    ) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(listNum).orElseThrow(() -> new IllegalArgumentException("post not found"));
        User user = userRepository.findByUserAuthId(user_auth_id);
        CommentLikesUser commentLikesUser = commentLikesUserRepository.findByCommentAndUser(comment, user);
        if (commentLikesUser == null) {
            comment.setLikes(comment.getLikes() + 1);
            commentLikesUser = new CommentLikesUser(comment, user);
            commentLikesUserRepository.save(commentLikesUser);
            commentRepository.save(comment);
            return "liked";
        } else {
            comment.setLikes(comment.getLikes() - 1);
            commentLikesUserRepository.delete(commentLikesUser);
            commentRepository.save(comment);
            return "like canceled";
        }
    }
}
