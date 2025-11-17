package com.multirkh.chimhahaclone.api.image.domain;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import com.multirkh.chimhahaclone.api.post.image.domain.PostImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
@Table(
        name = "image"
)
public class Image {
    @OneToMany(mappedBy = "thumbNailImage")
    private final Set<Post> thumbNailedPost = new HashSet<>();
    @OneToMany(mappedBy = "image")
    private final Set<PostImage> postImages = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String contentType;
    @Setter
    @Column(length = 512)
    private String url;
    @Setter
    private ZonedDateTime expirationDate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_image_id")
    private Image rawImage;
    @OneToOne(mappedBy = "rawImage")
    @Setter
    private Image thumbNailImage;
    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;


    public Image(String fileName, String contentType, String url, ZonedDateTime expirationDate) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.url = url;
        this.expirationDate = expirationDate;
    }

    public Image(Image rawImage, String url, ZonedDateTime expirationDate) {
        this.fileName = "";
        this.rawImage = rawImage;
        this.contentType = rawImage.getContentType();
        this.url = url;
        this.expirationDate = expirationDate;
    }
}
