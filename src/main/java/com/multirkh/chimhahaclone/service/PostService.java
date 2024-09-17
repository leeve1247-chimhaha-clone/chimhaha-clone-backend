package com.multirkh.chimhahaclone.service;

import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.repository.CommentRepository;
import com.multirkh.chimhahaclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void increaseComment(Post post) {
        if (post.getCommentsCount() == null || post.getCommentsCount() < 0)
            post.setCommentsCount(commentRepository.countCommentByPost(post));
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
    }

    public void decreaseComment(Post post) {
        if (post.getCommentsCount() == null || post.getCommentsCount() < 0)
            post.setCommentsCount(commentRepository.countCommentByPost(post));
        post.setCommentsCount(post.getCommentsCount() - 1);
        postRepository.save(post);
    }
}
