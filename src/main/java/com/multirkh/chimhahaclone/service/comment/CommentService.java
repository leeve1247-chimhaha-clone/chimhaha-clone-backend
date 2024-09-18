package com.multirkh.chimhahaclone.service.comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.dto.CommentReceived;
import com.multirkh.chimhahaclone.entity.*;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import com.multirkh.chimhahaclone.repository.CommentLikesUserRepository;
import com.multirkh.chimhahaclone.repository.CommentRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.multirkh.chimhahaclone.util.UtilStringJsonConverter.jsonNodeOf;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikesUserRepository commentLikesUserRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Comment createComment(CommentReceived request) {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        JsonNode jsonContent = request.getContent();
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new IllegalArgumentException("post not found"));

        Comment comment;
        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId()).orElse(null);
            comment = new Comment(jsonContent, post, user, 0, parentComment);
        } else {
            comment = new Comment(jsonContent, post, user, 0);
        }

        return commentRepository.save(comment);
    }

    public Comment updateComment(CommentReceived request) {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepository.findById(request.getParentCommentId()).orElseThrow();
        if (!comment.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("You are not the owner of this comment");
        comment.setContent(request.getContent());
        comment.setStatus(PostStatus.EDITED);
        return commentRepository.save(comment);
    }

    public Comment deleteComment(CommentReceived request) {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepository.findById(request.getParentCommentId()).orElseThrow( () -> new IllegalArgumentException("comment not found"));
        if (!comment.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("You are not the owner of this comment");
        comment.setContent(jsonNodeOf("{\"ops\": [{\"insert\": \"삭제된 댓글입니다\\n\"}]}"));
        comment.setStatus(PostStatus.DELETED);
        return commentRepository.save(comment);
    }

    public Integer updateCommentLikes(Map<String, Long> body){
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepository.findById(body.get("commentId")).orElseThrow(() -> new IllegalArgumentException("post not found"));
        CommentLikesUser commentLikesUser = commentLikesUserRepository.findByCommentAndUser(comment, user);
        if (commentLikesUser == null) {
            comment.setLikes(comment.getLikes() + 1);
            commentLikesUser = new CommentLikesUser(comment, user);
            commentLikesUserRepository.save(commentLikesUser);
            commentRepository.save(comment);
            return comment.getLikes();
        } else {
            // cancel like
            if (commentLikesUser.getLike()) {
                commentLikesUser.setLike(false);
                comment.setLikes(comment.getLikes() - 1);
            } else {
                // re-like
                commentLikesUser.setLike(true);
                comment.setLikes(comment.getLikes() + 1);
            }
            commentRepository.save(comment);
            return comment.getLikes();
        }
    }

    public void validateCommentForm(CommentReceived request) {
        if (request.getContent() == null) throw new IllegalArgumentException("content is null");
    }

}
