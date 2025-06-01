package com.multirkh.chimhahaclone.api.comment;

import com.multirkh.chimhahaclone.api.comment.dtos.CommentPageRequest;
import com.multirkh.chimhahaclone.api.comment.dtos.CommentDto;
import com.multirkh.chimhahaclone.api.comment.dtos.CommentReceived;
import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.post.PostService;
import jakarta.annotation.security.RolesAllowed;
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
        return commentService.getCommentTree(request);
    }

    @GetMapping("/get/comment/page-size")
    public Integer getCommentPageSize(
            @RequestParam(name = "postId") Long postId
    ){
        return commentService.getCommentPageSize(postId);
    }

    @PostMapping("/save/comment")
    @RolesAllowed("USER")
    public Integer createComment(
            @RequestBody CommentReceived request
    ) {
        commentService.validateCommentForm(request);
        Comment comment = commentService.createComment(request);
        postService.increaseCommentCount(comment.getPost());
        return commentService.getCommentPage(comment);
    }

    @PostMapping("/update/comment")
    @RolesAllowed("USER")
    public Integer updateComment(
            @RequestBody CommentReceived request
    ) {
        commentService.validateCommentForm(request);
        Comment comment = commentService.updateComment(request);
        return commentService.getCommentPage(comment);
    }

    @PostMapping("/delete/comment")
    @RolesAllowed("USER")
    public Integer deleteComment(
            @RequestBody CommentReceived request
    ) {
        Comment comment = commentService.deleteComment(request);
        postService.decreaseCommentCount(comment.getPost());
        return commentService.getCommentPage(comment);
    }


    @PostMapping("/comments/like")
    @RolesAllowed("USER")
    public Integer likeComment(
            @RequestBody Map<String, Long> body
    ) {
        return commentService.updateCommentLikes(body);
    }
}
