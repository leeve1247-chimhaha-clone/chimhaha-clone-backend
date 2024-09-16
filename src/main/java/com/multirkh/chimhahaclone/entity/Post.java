package com.multirkh.chimhahaclone.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.PostCategory;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;

    @Setter
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode jsonContent;

    @Setter
    private Integer views;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    private String titleImageFileName;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PostImage> postImages = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostLikesUser> postLikesUsers = new ArrayList<>();

    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;

    @Setter
    private Integer likes;

    //신규 생성
    public Post(String title, JsonNode jsonContent, User user, PostCategory postCategory, String titleImageFileName) {
        this.title = title;
        this.jsonContent = jsonContent;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.category = postCategory;
        this.status = PostStatus.POSTED;
        this.titleImageFileName = titleImageFileName;
    }

    //신규 생성 (이미지 없음)
    public Post(String title, JsonNode jsonContent, User user, PostCategory postCategory) {
        this.title = title;
        this.jsonContent = jsonContent;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.category = postCategory;
        this.status = PostStatus.POSTED;
    }
}
