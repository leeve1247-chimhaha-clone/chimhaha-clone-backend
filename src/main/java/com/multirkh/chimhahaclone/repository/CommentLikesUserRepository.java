package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.CommentLikesUser;
import com.multirkh.chimhahaclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesUserRepository extends JpaRepository<CommentLikesUser, Long> {
    CommentLikesUser findByCommentAndUser(Comment comment, User user);
}
