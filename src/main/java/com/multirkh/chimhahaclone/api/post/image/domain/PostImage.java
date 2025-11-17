package com.multirkh.chimhahaclone.api.post.image.domain;

import com.multirkh.chimhahaclone.api.image.domain.Image;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "post_image",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "image_id"})
)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    // 신규 생성
    public PostImage(Post post, Image image) {
        this.post = post;
        this.image = image;
        post.getPostImages().add(this);
        image.getPostImages().add(this);
    }
}
