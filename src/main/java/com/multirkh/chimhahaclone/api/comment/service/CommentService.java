package com.multirkh.chimhahaclone.api.comment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.api.comment.dtos.CommentPageRequest;
import com.multirkh.chimhahaclone.api.comment.dtos.CommentDto;
import com.multirkh.chimhahaclone.api.comment.dtos.CommentReceived;
import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.comment.likes.domain.CommentLikesUser;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.user.domain.User;
import com.multirkh.chimhahaclone.api.post.domain.PostStatus;
import com.multirkh.chimhahaclone.api.comment.likes.CommentLikesUserRepository;
import com.multirkh.chimhahaclone.api.comment.CommentRepository;
import com.multirkh.chimhahaclone.api.post.PostRepository;
import com.multirkh.chimhahaclone.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.multirkh.chimhahaclone.common.util.UtilStringJsonConverter.jsonNodeOf;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikesUserRepository commentLikesUserRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final Integer numCommentsPerPage = 10;

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

        Comment parentComment = comment.getParent();
        while (parentComment != null) {
            Long repliesCount = parentComment.getReplies_count();
            if (repliesCount == null) repliesCount = 0L;
            parentComment.setReplies_count( repliesCount + 1L);
            parentComment = comment.getParent();
        }
        return commentRepository.save(comment);
    }

    public Comment updateComment(CommentReceived request) {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepository.findById(request.getParentCommentId()).orElseThrow();
        if (!comment.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("You are not the owner of this comment");
        comment.setContent(request.getContent());
        comment.setStatus(PostStatus.EDITED);
        return commentRepository.save(comment);
    }

    public Comment deleteComment(CommentReceived request) {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepository.findById(request.getParentCommentId()).orElseThrow(() -> new IllegalArgumentException("comment not found"));
        if (!comment.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("You are not the owner of this comment");
        comment.setContent(jsonNodeOf("{\"root\": {\"type\": \"root\", \"format\": \"\", \"indent\": 0, \"version\": 1, \"children\": [{\"type\": \"paragraph\", \"format\": \"\", \"indent\": 0, \"version\": 1, \"children\": [{\"mode\": \"normal\", \"text\": \"삭제된 댓글입니다.\", \"type\": \"text\", \"style\": \"\", \"detail\": 0, \"format\": 0, \"version\": 1}], \"direction\": \"ltr\", \"textStyle\": \"\", \"textFormat\": 0}], \"direction\": \"ltr\"}}"));
        comment.setStatus(PostStatus.DELETED);
        Comment parentComment = comment.getParent();
        Long repliesCount = comment.getReplies_count();
        if (repliesCount == null) repliesCount = 1L;
        else repliesCount = repliesCount + 1L;
        while (parentComment != null) {
            parentComment.setReplies_count(parentComment.getReplies_count() - repliesCount);
            parentComment = comment.getParent();
        }
        return commentRepository.save(comment);
    }

    public Integer updateCommentLikes(Map<String, Long> body) {
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

    public List<CommentDto> getCommentTree(@NotNull CommentPageRequest request) {
        if (request.getPostId() == null) {
            throw new IllegalArgumentException("postId is null");
        }

        List<CommentPage> commentPageList = commentRepository.getCommentPages(request.getPostId(), numCommentsPerPage);

        if (request.getCommentId() != null) {
            CommentPage commentPage = commentPageList
                    .stream().filter(cp ->
                            cp.getStartId() <= request.getCommentId() &&
                                    cp.getEndId() >= request.getCommentId()
                    )
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("comment not found"));
            return getCommentDTOList(request, commentPage);
        }

        if (request.getPageNum() != null) {
            int pageIndex = Math.toIntExact(request.getPageNum()) - 1;
            if (pageIndex < 0 || pageIndex >= commentPageList.size()) return new ArrayList<>();
            CommentPage commentPage = commentPageList.get(pageIndex);
            return getCommentDTOList(request, commentPage);
        }

        CommentPage commentPage = commentPageList.getFirst();
        return getCommentDTOList(request, commentPage);
    }

    @NotNull
    private List<CommentDto> getCommentDTOList(@NotNull CommentPageRequest request, CommentPage commentPage) {
        List<Comment> comments = commentRepository.getRecursiveCommentsByStartEndId(commentPage.getStartId(), commentPage.getEndId(), request.getPostId());
        List<Comment> selfLiked = commentRepository.getSelfLiked(comments);
        Set<Long> selfLikedId = selfLiked.stream().map(Comment::getId).collect(Collectors.toSet());
        List<CommentDto> commentDtoFlat = comments.stream().map(c -> {
            if (selfLikedId.contains(c.getId())) {
                return new CommentDto(c, true);
            } else {
                return new CommentDto(c, false);
            }
        }).toList();
        Map<Long, CommentDto> commentDtoMap = commentDtoFlat.stream().collect(Collectors.toMap(CommentDto::getId, c -> c));

        for (CommentDto commentDto : commentDtoFlat) {
            if (commentDto.getParentId() != null) {
                commentDtoMap.get(commentDto.getParentId()).getChildren().add(commentDto);
            }
        }
        return commentDtoFlat.stream().filter(commentDto -> commentDto.getParentId() == null).collect(Collectors.toList());
    }

    public Integer getCommentPageSize(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
        Integer commentsSum = commentRepository.countCommentsByPost(post);
        if (commentsSum == null) commentsSum = 0;
        return (int) Math.ceil((double) commentsSum / numCommentsPerPage);
    }

    public Integer getCommentPage(@NotNull Comment comment) {
        List<CommentPage> commentPageList = commentRepository.getCommentPages(comment.getPost().getId(), numCommentsPerPage);
        int pageNum = getPageNum(comment, commentPageList);
        if (pageNum < 1) {
            throw new IllegalArgumentException("comment not found in comment pages");
        }
        return pageNum;
    }

    private int getPageNum(@NotNull Comment comment, List<CommentPage> commentPageList) {
        while (comment.getParent() != null) {
            comment = comment.getParent();
        }
        if (comment.getStatus() == PostStatus.DELETED) {
            return findIntervalIndexForDeleted(commentPageList, comment.getId()) + 1;
        }
        return findIntervalIndex(commentPageList, comment.getId()) + 1;
    }

    private Integer findIntervalIndexForDeleted(List<CommentPage> commentPageList, Long commentId) {
        Comparator<CommentPage> comparator = Comparator.comparing(CommentPage::getStartId);
        int i = Collections.binarySearch(commentPageList, new CommentPage(commentId, commentId), comparator);
        if ( i >= 1){
            return i-1;
        } else {
            int nearestPoint = -i;
            return Math.max(nearestPoint - 2, 0);
        }
    }

    private Integer findIntervalIndex(List<CommentPage> commentPageList, Long commentId) {
        Comparator<CommentPage> comparator = (index1, index2) -> {
            if (index1.getEndId() < index2.getStartId()) return -1;
            if (index1.getStartId() > index2.getEndId()) return 1;
            return 0;
        };
        return Collections.binarySearch(commentPageList, new CommentPage(commentId, commentId), comparator);
    }
}
