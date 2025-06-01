package com.multirkh.chimhahaclone.api.comment.likes;

import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.comment.likes.domain.CommentLikesUser;
import com.multirkh.chimhahaclone.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesUserRepository extends JpaRepository<CommentLikesUser, Long> {
    CommentLikesUser findByCommentAndUser(Comment comment, User user);
}
