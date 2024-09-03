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

@Entity
@Getter
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private String content;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode jsonContent;

    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;

    private Integer likes;

    public Post(String title, String body, String content, Integer views, PostCategory category, User user, Integer likes) {
        this.title = title;
        this.body = body;
        this.content = content;
        this.views = views;
        this.category = category;
        this.user = user;
        this.likes = likes;
    }

    public Post(String title, JsonNode jsonContent, User user, PostCategory postCategory){
        this.title = title;
        this.jsonContent = jsonContent;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.category = postCategory;
        this.status = PostStatus.POSTED;
    }
}
