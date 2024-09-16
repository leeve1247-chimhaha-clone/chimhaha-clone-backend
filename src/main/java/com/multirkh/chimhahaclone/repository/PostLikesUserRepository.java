package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostLikesUser;
import com.multirkh.chimhahaclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikesUserRepository extends JpaRepository<PostLikesUser, Long> {
    PostLikesUser findByPostAndUser(Post post, User user);
}
