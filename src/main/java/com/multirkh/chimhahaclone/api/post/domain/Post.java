package com.multirkh.chimhahaclone.api.post.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.api.comment.domain.Comment;
import com.multirkh.chimhahaclone.api.post.category.domain.PostCategory;
import com.multirkh.chimhahaclone.api.post.likes.domain.PostLikesUser;
import com.multirkh.chimhahaclone.api.user.domain.User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Post {

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PostLikesUser> postLikesUsers = new HashSet<>();
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
    private Integer commentsCount;
    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;

    @Setter
    private Integer likes;

    //신규 생성
    public Post(String title, JsonNode jsonContent, User user, PostCategory postCategory) {
        this.title = title;
        this.jsonContent = jsonContent;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.category = postCategory;
        this.status = PostStatus.POSTED;
        this.commentsCount = 0;
    }
}
