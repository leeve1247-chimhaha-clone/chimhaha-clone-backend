package com.multirkh.chimhahaclone.api.post;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.status = 'POSTED' ORDER BY p.createdDate DESC")
    List<Post> findAllByOrderByCreatedDate(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category.key = :category and p.status = 'POSTED' ORDER BY p.createdDate DESC")
    List<Post> findAllByOrderByCreatedDateWhere(String category, Pageable pageable);

    @Query("SELECT p From Post p left join PostLikesUser plu on p = :post and plu.user = p.user and plu.post = p where p.status != 'DELETED' and plu.like = true ")
    Post findSelfLiked(Post post);

    @Query("Select p from Post p where p.id = :postId and p.status != 'DELETED'")
    Optional<Post> findByIdPostStatusNotDeleted(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.status = 'DELETED'")
    List<Post> findAllByStatus_Deleted();
}
