package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.dto.CommentReceived;
import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.service.comment.CommentService;
import com.multirkh.chimhahaclone.service.post.PostService;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/get/comment/page")
    public List<CommentDto> getCommentPage(
            @RequestParam(name = "commentId", required = false) Long commentId,
            @RequestParam(name = "postId") Long postId,
            @RequestParam(name = "pageNum", required = false) Long pageNum
    ) {
        CommentPageRequest request = new CommentPageRequest(postId, pageNum, commentId);
        return commentService.getCommentPage(request);
    }

    @GetMapping("/get/comment/page-size")
    public Integer getCommentPageSize(
            @RequestParam(name = "postId") Long postId
    ){
        return commentService.getCommentPageSize(postId);
    }

    @PostMapping("/save/comment")
    @RolesAllowed("USER")
    public CommentDto createComment(
            @RequestBody CommentReceived request
    ) {
        commentService.validateCommentForm(request);
        Comment comment = commentService.createComment(request);
        postService.increaseCommentCount(comment.getPost());
        return new CommentDto(comment);
    }

    @PostMapping("/update/comment")
    @RolesAllowed("USER")
    public CommentDto updateComment(
            @RequestBody CommentReceived request
    ) {
        commentService.validateCommentForm(request);
        Comment comment = commentService.updateComment(request);
        return new CommentDto(comment);
    }

    @PostMapping("/delete/comment")
    @RolesAllowed("USER")
    public CommentDto deleteComment(
            @RequestBody CommentReceived request
    ) {
        Comment comment = commentService.deleteComment(request);
        postService.decreaseCommentCount(comment.getPost());
        return new CommentDto(comment);
    }


    @PostMapping("/comments/like")
    @RolesAllowed("USER")
    public Integer likeComment(
            @RequestBody Map<String, Long> body
    ) {
        return commentService.updateCommentLikes(body);
    }
}
