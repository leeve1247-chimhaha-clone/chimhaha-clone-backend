// src/main/java/com/multirkh/chimhahaclone/entity/User.java
package com.multirkh.chimhahaclone.api.user.domain;

import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.comment.likes.domain.CommentLikesUser;
import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.likes.domain.PostLikesUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`user`")
public class User {
    @OneToMany(mappedBy = "user") // mappedBy: 연관관계의 주인이 아님을 나타냄 (읽기 전용) DB 에선 안보임
    private final List<Post> posts = new ArrayList<>();
    @OneToMany(mappedBy = "user") // mappedBy: 연관관계의 주인이 아님을 나타냄 (읽기 전용) DB 에선 안보임
    private final List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private final Set<PostLikesUser> postLikesUsers = new HashSet<>();
    @OneToMany(mappedBy = "user")
    private final Set<CommentLikesUser> commentLikesUser = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Column(nullable = true)
    private String userName;
    @Column(nullable = false, unique = true, updatable = false)
    private String userAuthId;

    public User(String userAuthId) {
        this.userAuthId = userAuthId;
    }

    public User(String userAuthId, String userName) {
        this.userAuthId = userAuthId;
        this.userName = userName;
    }
}