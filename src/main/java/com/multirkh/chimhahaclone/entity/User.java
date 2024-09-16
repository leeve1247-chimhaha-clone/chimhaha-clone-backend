// src/main/java/com/multirkh/chimhahaclone/entity/User.java
package com.multirkh.chimhahaclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = true)
    private String userName;

    @Column(nullable = false)
    private String userAuthId;

    @OneToMany(mappedBy = "user") // mappedBy: 연관관계의 주인이 아님을 나타냄 (읽기 전용) DB 에선 안보임
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user") // mappedBy: 연관관계의 주인이 아님을 나타냄 (읽기 전용) DB 에선 안보임
    private final List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_like_user_id")
    private PostLikesUser postLikesUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_like_user_id")
    private CommentLikesUser commentLikesUser;

    public User(String userAuthId){
        this.userAuthId = userAuthId;
    }

    public User(String userAuthId, String userName){
        this.userAuthId = userAuthId;
        this.userName = userName;
    }
}