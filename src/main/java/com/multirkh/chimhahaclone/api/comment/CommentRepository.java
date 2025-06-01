package com.multirkh.chimhahaclone.api.comment;

import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.comment.service.CommentPage;
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
                                "SUM(COALESCE(replies_count, 0) + 1) over (ORDER BY id) as accumulated_total " +
                                "from comments " +
                                "where post_id = :postId " +
                                "and parent_id is null " +
                                "and status != 'DELETED' " +
                                "), " +
                        "cte2 as (select *, ceiling(accumulated_total / :groupSize) as page from cte) " +
                    "select min(id) as start_id, max(id) as end_id " +
                    "from cte2 " +
                    "group by page;"
            , nativeQuery = true
    )
    List<CommentPage> getCommentPages(@Param("postId") Long postId, @Param("groupSize") Integer groupSize);

    @Query(value = "select c from Comment c left join CommentLikesUser clu on c.user = clu.user and c = clu.comment where clu.like = true and c in :comments")
    List<Comment> getSelfLiked(List<Comment> comments);


    @Query("select sum(coalesce(c.replies_count, 0))+count(c) from Comment c where c.parent is null and c.post = :post and c.status != 'DELETED'")
    Integer countCommentsByPost(Post post);

    @Query(value = "with RECURSIVE cte as (select c.* from comments c where (c.parent_id IS NULL and c.post_id = :postId and :startId <= c.id and c.id <= :endId and c.status != 'DELETED') union all select t.* from comments t inner join cte on t.parent_id = cte.id where t.status != 'DELETED') select * from cte;" , nativeQuery = true)
    List<Comment> getRecursiveCommentsByStartEndId(@Param("startId") Long startId, @Param("endId") Long endId, @Param("postId") Long postId);
}
