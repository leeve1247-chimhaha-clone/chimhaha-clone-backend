package com.multirkh.chimhahaclone.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment_likes_user")
public class CommentLikesUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Setter
    @Column(name = "comment_like") // like 는 예약어이므로 컬럼명을 comment_like 로 지정
    private Boolean like;

    public CommentLikesUser(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
        this.like = true;
    }
}
