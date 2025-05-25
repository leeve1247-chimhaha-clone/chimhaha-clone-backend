package com.multirkh.chimhahaclone.service.comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.controller.CommentPageRequest;
import com.multirkh.chimhahaclone.dto.CommentDto;
import com.multirkh.chimhahaclone.dto.CommentReceived;
import com.multirkh.chimhahaclone.entity.*;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import com.multirkh.chimhahaclone.repository.CommentLikesUserRepository;
import com.multirkh.chimhahaclone.repository.CommentRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<CommentDto> getCommentPage(@NotNull CommentPageRequest request){
        if (request.getPostId() == null){
            throw new IllegalArgumentException("postId is null");
        }
        List<CommentPage> commentPageList = commentRepository.getCommentPages(request.getPostId());

        if (request.getCommentId() != null){
            CommentPage commentPage = commentPageList.stream().filter(cp -> cp.getStartId() <= request.getCommentId() && cp.getEndId() >= request.getCommentId()).findFirst().orElseThrow(() -> new IllegalArgumentException("comment not found"));
            return getCommentDTOList(request, commentPage);
        }

        if (request.getPageNum() != null) {
            CommentPage commentPage = commentPageList.get(Math.toIntExact(request.getPageNum()) - 1);
            return getCommentDTOList(request, commentPage);
        }

        CommentPage commentPage = commentPageList.getFirst();
        return getCommentDTOList(request, commentPage);
    }

    @NotNull
    private List<CommentDto> getCommentDTOList(@NotNull CommentPageRequest request, CommentPage commentPage) {
        List<Comment> comments = commentRepository.getRecursiveCommentsByStartEndId(commentPage.getStartId(), commentPage.getEndId(), request.getPostId());
        List<CommentDto> commentDtoFlat = comments.stream().map(CommentDto::new).toList(); // 첫 id 에서 조회 쿼리 발생 이유는 모름
        Map<Long, CommentDto> commentDtoMap = commentDtoFlat.stream().collect(Collectors.toMap(CommentDto::getId, c -> c));
        for (CommentDto commentDto : commentDtoFlat){
            if (commentDto.getParentId() != null){
                commentDtoMap.get(commentDto.getParentId()).getChildren().add(commentDto);
            }
        }
        return commentDtoFlat.stream().filter(commentDto -> commentDto.getParentId() == null).collect(Collectors.toList());
    }
}
