package com.multirkh.chimhahaclone.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> children = new ArrayList<>();

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    @Setter
    private JsonNode content;

    @Setter
    private Integer likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CommentLikesUser> commentLikesUser = new ArrayList<>();

    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;

    public Comment(JsonNode content, Post post, User user, Integer likes) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.likes = likes;
        this.status = PostStatus.POSTED;

        if (!post.getComments().contains(this)) {
            post.getComments().add(this);
        }
        ;
        if (!user.getComments().contains(this)) {
            user.getComments().add(this);
        }
        ;
    }

    // 답글
    public Comment(JsonNode content, Post post, User user, Integer likes, Comment parent) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.likes = likes;
        this.parent = parent;
        this.status = PostStatus.POSTED;

        if (!post.getComments().contains(this)) {
            post.getComments().add(this);
        }
        ;
        if (!user.getComments().contains(this)) {
            user.getComments().add(this);
        }
        ;
        if (!parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
        ;
    }
}
