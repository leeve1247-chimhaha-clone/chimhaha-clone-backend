package com.multirkh.chimhahaclone.api.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.api.post.category.domain.PostCategory;
import com.multirkh.chimhahaclone.api.post.category.PostCategoryRepository;
import com.multirkh.chimhahaclone.api.post.likes.dto.LikeRequest;
import com.multirkh.chimhahaclone.api.post.dto.PostDetailDto;
import com.multirkh.chimhahaclone.api.post.dto.PostListComponentDto;
import com.multirkh.chimhahaclone.api.post.dto.PostReceived;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.likes.domain.PostLikesUser;
import com.multirkh.chimhahaclone.api.user.domain.User;
import com.multirkh.chimhahaclone.api.post.domain.PostStatus;
import com.multirkh.chimhahaclone.common.exception.FindDeletedPostException;
import com.multirkh.chimhahaclone.api.comment.CommentRepository;
import com.multirkh.chimhahaclone.api.post.likes.PostLikesUserRepository;
import com.multirkh.chimhahaclone.api.user.UserRepository;
import com.multirkh.chimhahaclone.api.image.ImageService;
import com.multirkh.chimhahaclone.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikesUserRepository postLikesUserRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final ImageService imageService;
    private final UserService userService;

    public String createPost(PostReceived request) {
        User user = userService.getUser();
        String title = request.getTitle();
        PostCategory postCategory = postCategoryRepository.findByKey(request.getPostCategoryKey());
        JsonNode jsonContent = request.getContent();

        Post post = postRepository.save(new Post(title, jsonContent, user, postCategory));

        imageService.createThumbnailImage(post);
        imageService.createPostImages(post);

        return post.getId().toString();
    }

    public String updatePost(PostReceived request) {
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow();
        post.setTitle(request.getTitle());
        post.setCategory(postCategoryRepository.findByKey(request.getPostCategoryKey()));
        post.setJsonContent(request.getContent());
        Post updatedPost = postRepository.save(post); //postImage not yet updated

        imageService.updateThumbnailImage(updatedPost, request);
        imageService.updatePostImage(updatedPost, request);

        return updatedPost.getId().toString();
    }

    public String deletePost(Post post) {
        post.setJsonContent(null);
        post.setStatus(PostStatus.DELETED);
        Post savedPost = postRepository.save(post);

        imageService.deleteThumbnailImage(savedPost);
        imageService.deletePostImage(savedPost);

        return savedPost.getId().toString();
    }

    public void validateCreatePost(PostReceived request) {
        validatePostForm(request);
    }

    public void validateUpdatePost(PostReceived request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (!post.getUser().getUserAuthId().equals(user_auth_id))
            throw new IllegalArgumentException("user is not matched");
        validatePostForm(request);
    }

    public Post validateDeletePost(PostReceived request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(Long.valueOf(request.getPostId())).orElseThrow(() -> new IllegalArgumentException("post not found"));
        if (!post.getUser().getUserAuthId().equals(user_auth_id))
            throw new IllegalArgumentException("user is not matched");
        return post;
    }

    private void validatePostForm(PostReceived request) {
        if (request.getTitle() == null) throw new IllegalArgumentException("title is null");
        if (request.getContent() == null) throw new IllegalArgumentException("content is null");
        if (request.getPostCategoryKey() == null) throw new IllegalArgumentException("post category is null");
        if (postCategoryRepository.findPostCategoryByKey(request.getPostCategoryKey()).isEmpty())
            throw new IllegalArgumentException("post category is not exist");
    }

    public PostDetailDto findPost(Long postNum) {
        Post post = postRepository.findByIdPostStatusNotDeleted(postNum).orElseThrow(() -> new IllegalArgumentException("Post not Found"));
        if (post.getStatus() == PostStatus.DELETED) throw new FindDeletedPostException();
        Post selfLikedPost = postRepository.findSelfLiked(post);
        boolean selfLiked = selfLikedPost != null;
        JsonNode appliedPresignedUrlToImageSrcContent = imageService.applyPresignedUrlToImageSrc(post.getJsonContent());
        return new PostDetailDto(post, appliedPresignedUrlToImageSrcContent, selfLiked);
    }

    public List<PostListComponentDto> findPostList() {
        PageRequest pageRequest = PageRequest.of(0, 30);
        return postRepository
                .findAllByOrderByCreatedDate(pageRequest)
                .stream()
                .map(PostListComponentDto::new)
                .toList();
    }


    public List<PostListComponentDto> findPostList(String category) {
        PageRequest pageRequest = PageRequest.of(0, 30);
        return postRepository
                .findAllByOrderByCreatedDateWhere(category, pageRequest)
                .stream()
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

    public Integer updateLikesCount(LikeRequest request) {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new IllegalArgumentException("post not found"));
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
