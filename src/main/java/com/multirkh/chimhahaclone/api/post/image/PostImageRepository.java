package com.multirkh.chimhahaclone.api.post.image;

import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.image.domain.PostImage;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("SELECT pi FROM PostImage pi left join fetch pi.post p left join fetch pi.image i left join fetch i.postImages ipi WHERE pi.post = :post")
    Set<PostImage> findByPost(Post post);

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi in :postImages")
    void deleteAllByPostImages(Set<PostImage> postImages);

    Set<PostImage> findAllByPostAndImageIn(Post post, Collection<Image> images);


    @Query("select distinct pi.image from PostImage pi where pi.post = :post")
    Set<Image> findDistinctImageByPost(@Param("post") Post post);
}
