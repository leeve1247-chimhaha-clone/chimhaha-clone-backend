package com.multirkh.chimhahaclone.service.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.PostCategory;
import com.multirkh.chimhahaclone.dto.PostDetailDto;
import com.multirkh.chimhahaclone.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.dto.PostReceived;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostLikesUser;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.exception.FindDeletedPostException;
import com.multirkh.chimhahaclone.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikesUserRepository postLikesUserRepository;
    private final PostCategoryRepository postCategoryRepository;

    public Post createPost(PostReceived request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(user_auth_id);
        PostCategory postCategory = postCategoryRepository.findByName(request.getPostCategoryName());
        JsonNode jsonContent = request.getContent();
        String titleImageFileName = request.getTitleImageFileName();
        String title = request.getTitle();
        return postRepository.save(new Post(title, jsonContent, user, postCategory, titleImageFileName));
    }

    public String updatePost(Post post, PostReceived request) {
        post.setTitle(request.getTitle());
        post.setJsonContent(request.getContent());
        post.setCategory(postCategoryRepository.findByName(request.getPostCategoryName()));
        post.setTitleImageFileName(request.getTitleImageFileName());
        Post savedPost = postRepository.save(post);
        return savedPost.getId().toString();
    }

    public String deletePost(Post post) {
        post.setJsonContent(null);
        post.setStatus(PostStatus.DELETED);
        Post savedPost = postRepository.save(post);
        return savedPost.getId().toString();
    }

    public void validateCreatePost(PostReceived request) {
        validatePostForm(request);
    }

    public Post validateUpdatePost(PostReceived request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (!post.getUser().getUserAuthId().equals(user_auth_id)) throw new IllegalArgumentException("user is not matched");
        validatePostForm(request);
        return post;
    }

    public Post validateDeletePost(PostReceived request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (!post.getUser().getUserAuthId().equals(user_auth_id)) throw new IllegalArgumentException("user is not matched");
        return post;
    }

    private void validatePostForm(PostReceived request) {
        if (request.getTitle() == null) throw new IllegalArgumentException("title is null");
        if (request.getContent() == null) throw new IllegalArgumentException("content is null");
        if (request.getPostCategoryName() == null) throw new IllegalArgumentException("postCategoryName is null");
        if (request.getTitleImageFileName() == null) throw new IllegalArgumentException("titleImageFileName is null");
    }

    public PostDetailDto findPost(Long listNum) {
        Post postEntity = postRepository.findById(listNum).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (postEntity.getStatus() == PostStatus.DELETED) throw new FindDeletedPostException();
        return new PostDetailDto(postEntity);
    }

    public List<PostListComponentDto> findPostList() {
        return postRepository
                .findAllByOrderByCreatedDateDesc()
                .stream()
                .filter(post -> post
                        .getStatus() == PostStatus.POSTED
                )
                .map(PostListComponentDto::new)
                .toList();
    }

    public void increaseCommentCount(Post post) {
        if (post.getCommentsCount() == null || post.getCommentsCount() < 0)
            post.setCommentsCount(commentRepository.countCommentByPost(post));
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
    }

    public void decreaseCommentCount(Post post) {
        if (post.getCommentsCount() == null || post.getCommentsCount() < 0)
            post.setCommentsCount(commentRepository.countCommentByPost(post));
        post.setCommentsCount(post.getCommentsCount() - 1);
        postRepository.save(post);
    }

    public Integer updateLikesCount(Map<String, Long> body) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(body.get("num")).orElseThrow(() -> new IllegalArgumentException("post not found"));
        User user = userRepository.findByUserAuthId(user_auth_id);
        PostLikesUser postLikesUser = postLikesUserRepository.findByPostAndUser(post, user);
        if (postLikesUser == null) {
            post.setLikes(post.getLikes() + 1);
            postLikesUser = new PostLikesUser(post, user);
            postLikesUserRepository.save(postLikesUser);
            postRepository.save(post);
            return post.getLikes();
        } else {
            System.out.println("postLikesUser.getLike() = " + postLikesUser.getLike());
            System.out.println("postLikesUser.getLike().equals(true) = " + postLikesUser.getLike().equals(true));
            // cancel like
            if (postLikesUser.getLike()) {
                postLikesUser.setLike(false);
                post.setLikes(post.getLikes() - 1);
            } else {
                // re-like
                postLikesUser.setLike(true);
                post.setLikes(post.getLikes() + 1);
            }
            postRepository.save(post);
            return post.getLikes();
        }
    }
}
