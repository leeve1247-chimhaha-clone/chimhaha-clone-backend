package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedDateDesc();
    @Query("SELECT p FROM Post p WHERE p.category.key = :category ORDER BY p.createdDate DESC")
    List<Post> findAllByOrderByCreatedDateWhere(String category);

    @Query("SELECT p From Post p left join PostLikesUser plu on p = :post and plu.user = p.user and plu.post = p where p.status != 'DELETED' and plu.like = true ")
    Post findSelfLiked(Post post);

    @Query("Select p from Post p where p.id = :postId and p.status != 'DELETED'")
    Optional<Post> findByIdPostStatusNotDeleted(@Param("postId") Long postId);
}
