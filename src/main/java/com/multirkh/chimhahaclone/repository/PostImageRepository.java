package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.Image;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("SELECT pi FROM PostImage pi left join fetch pi.post p left join fetch pi.image i left join fetch i.postImages ipi WHERE pi.post = :post")
    Set<PostImage> findByPost(Post post);

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi in :postImages")
    void deleteAllByPostImages(Set<PostImage> postImages);

    Set<PostImage> findAllByPostAndImageIn(Post post, Collection<Image> images);


    @Query("select distinct pi.image from PostImage pi where pi.post = :post")
    Set<Image> findDistinctImageByPost(@Param("post")Post post);
}
