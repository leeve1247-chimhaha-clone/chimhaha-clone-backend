package com.multirkh.chimhahaclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_likes_user")
public class PostLikesUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Setter
    @Column(name = "post_like") // like 는 예약어이므로 컬럼명을 post_like 로 지정
    private Boolean like;

    public PostLikesUser(Post post, User user) {
        this.post = post;
        this.user = user;
        this.like = true;
    }
}
