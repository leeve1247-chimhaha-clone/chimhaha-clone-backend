package com.multirkh.chimhahaclone.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.CommentLikesUser;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.*;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.multirkh.chimhahaclone.util.UtilStringJsonConverter.jsonNodeOf;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesUserRepository commentLikesUserRepository;

    @PostMapping("/update/comment")
    @RolesAllowed("USER")
    public CommentDto updateComment(
            @RequestBody CommentReceived request
    ) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(name);
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow();

        if (comment.getUser().getId().equals(user.getId())) {
            comment.setContent(request.getContent());
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            throw new IllegalArgumentException("You are not the owner of this comment");
        }
    }

    @PostMapping("/delete/comment")
    @RolesAllowed("USER")
    public CommentDto deleteComment(
            @RequestBody CommentReceived request
    ) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(name);
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow();
        if (comment.getUser().getId().equals(user.getId())) {
            comment.setContent(jsonNodeOf("{\"ops\": [{\"insert\": \"삭제된 댓글입니다\\n\"}]}"));
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            throw new IllegalArgumentException("You are not the owner of this comment");
        }
    }

    @PostMapping("/save/comment")
    @RolesAllowed("USER")
    public CommentDto saveComment(
            @RequestBody CommentReceived request
    ) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(name);
        JsonNode jsonContent = request.getContent();
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        if (request.getCommentId() == null) {
            Comment comment = new Comment(jsonContent, post, user, 0);
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            Comment parent = commentRepository.findById(request.getCommentId()).orElse(null);
            Comment comment = new Comment(jsonContent, post, user, 0, parent);
            commentRepository.save(comment);
            return new CommentDto(comment);
        }
    }

    @PostMapping("/comments/like")
    @RolesAllowed("USER")
    public Integer likeComment(
            @RequestBody Map<String, Long> body
    ) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(body.get("num")).orElseThrow(() -> new IllegalArgumentException("post not found"));
        User user = userRepository.findByUserAuthId(user_auth_id);
        CommentLikesUser commentLikesUser = commentLikesUserRepository.findByCommentAndUser(comment, user);
        if (commentLikesUser == null) {
            comment.setLikes(comment.getLikes() + 1);
            commentLikesUser = new CommentLikesUser(comment, user);
            commentLikesUserRepository.save(commentLikesUser);
            commentRepository.save(comment);
            return comment.getLikes();
        } else {
            if (commentLikesUser.getLike()) {
                commentLikesUser.setLike(false);
                comment.setLikes(comment.getLikes() - 1);
                commentRepository.save(comment);
                return comment.getLikes();
            } else {
                commentLikesUser.setLike(true);
                comment.setLikes(comment.getLikes() + 1);
                commentRepository.save(comment);
                return comment.getLikes();
            }
        }
    }
}
