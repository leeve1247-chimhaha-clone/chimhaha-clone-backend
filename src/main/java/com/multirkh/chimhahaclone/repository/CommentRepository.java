package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countCommentByPost(Post post);

}
