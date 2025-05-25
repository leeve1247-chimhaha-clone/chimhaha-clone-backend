package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Comment;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.service.comment.CommentPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countCommentByPost(Post post);

    @Query("select c from Comment c")
    List<Comment> getCommentPageByCommentId(Long commentId);

    @Query(value =  "with cte as (select id, " +
            "                    SUM(COALESCE(replies_count, 0) + 1) over (ORDER BY id) as accumulated_total " +
            "             from comments " +
            "             where post_id = :postId " +
            "               and parent_id is null), " +
            "     cte2 as (select *, ceiling(accumulated_total / :groupSize) as page from cte) " +
            "select min(id) as start_id, max(id) as end_id " +
            "from cte2\n " +
            "group by page;"
            , nativeQuery = true
    )
    List<CommentPage> getCommentPages(@Param("postId") Long postId, @Param("groupSize") Integer groupSize);

    @Query("select sum(coalesce(c.replies_count, 0))+count(c) from Comment c where c.parent is null and c.post = :post")
    Integer countCommentsByPost(Post post);

    @Query(value = "with RECURSIVE cte as (select c.* from comments c where (c.parent_id IS NULL and c.post_id = :postId and :startId <= c.id and c.id <= :endId) union all select t.* from comments t inner join cte on t.parent_id = cte.id) select * from cte;" , nativeQuery = true)
    List<Comment> getRecursiveCommentsByStartEndId(@Param("startId") Long startId, @Param("endId") Long endId, @Param("postId") Long postId);

    @Query(value = "with RECURSIVE cte as (select c.* from comments c where (c.parent_id IS NULL and c.post_id = :postId) union all select t.* from comments t inner join cte on t.parent_id = cte.id) select * from cte;" , nativeQuery = true)
    List<Comment> getRecursiveCommentsByPostId(@Param("postId") Long postId);
}
