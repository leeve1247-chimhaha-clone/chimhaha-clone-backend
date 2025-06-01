package com.multirkh.chimhahaclone.api.post.likes;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.likes.domain.PostLikesUser;
import com.multirkh.chimhahaclone.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikesUserRepository extends JpaRepository<PostLikesUser, Long> {
    PostLikesUser findByPostAndUser(Post post, User user);
}
