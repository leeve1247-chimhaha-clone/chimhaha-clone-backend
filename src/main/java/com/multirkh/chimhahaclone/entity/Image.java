package com.multirkh.chimhahaclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Image {
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

    public Image(String fileName, String contentType, String url, ZonedDateTime expirationDate) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.url = url;
        this.expirationDate = expirationDate;
    }

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
    private final Set<PostImage> postImages = new HashSet<>();

    @CreatedDate
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime editedDate;
}
