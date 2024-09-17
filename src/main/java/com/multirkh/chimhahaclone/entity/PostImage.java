package com.multirkh.chimhahaclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    @Setter
    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @Setter
    private boolean mainImage;

    // 신규 생성
    public PostImage(Post post, Image image, ImageStatus status) {
        this.post = post;
        this.image = image;
        this.status = status;
        this.mainImage = false;
        post.getPostImages().add(this);
        image.getPostImages().add(this);
    }
}
