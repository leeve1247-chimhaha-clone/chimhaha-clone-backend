package com.multirkh.chimhahaclone.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.multirkh.chimhahaclone.category.entity.PostCategory;
import com.multirkh.chimhahaclone.entity.enums.PostStatus;
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
import java.util.stream.Collectors;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumb_nail_image_id")
    private Image thumbNailImage;

    @Setter
    private Integer commentsCount;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PostImage> postImages = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<PostLikesUser> postLikesUsers = new HashSet<>();

    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;

    @Setter
    private Integer likes;

    //신규 생성
    public Post(String title, JsonNode jsonContent, User user, PostCategory postCategory, Image thumbNailImage) {
        this.title = title;
        this.jsonContent = jsonContent;
        this.user = user;
        this.views = 0;
        this.likes = 0;
        this.category = postCategory;
        this.status = PostStatus.POSTED;
        this.addThumbNailImage(thumbNailImage);
        this.commentsCount = 0;
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
        this.commentsCount = 0;
    }

    public void addPostImage(Image image) {
        PostImage postImage = new PostImage(this, image);
        this.postImages.add(postImage);
        image.getPostImages().add(postImage);
    }

    public void addPostImages(Set<Image> images) {
        for (Image image : images) {
            addPostImage(image);
        }
    }

    public void removePostImage(Image image) {
        Set<PostImage> postImage = this.postImages.stream().filter(p -> p.getImage().equals(image)).collect(Collectors.toSet());
        image.getPostImages().removeAll(postImage);
        this.postImages.removeAll(postImage);
    }

    public void removePostImages(Set<Image> images) {
        Set<PostImage> toBeRemovedPostImages = this.postImages.stream().filter(p -> images.contains(p.getImage())).collect(Collectors.toSet());
        this.postImages.removeAll(toBeRemovedPostImages);
    }

    public void addThumbNailImage(Image thumbNailImage) {
        this.thumbNailImage = thumbNailImage;
        if (thumbNailImage != null) {
            thumbNailImage.getThumbNailedPost().add(this);
        }
    }

    public void removeThumbNailImage() {
        thumbNailImage.getThumbNailedPost().remove(this);
        this.thumbNailImage = null;
    }
}
