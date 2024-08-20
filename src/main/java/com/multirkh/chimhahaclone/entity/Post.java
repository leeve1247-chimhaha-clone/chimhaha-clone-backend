package com.multirkh.chimhahaclone.entity;


import com.multirkh.chimhahaclone.category.PostCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private String content;

    private Integer views;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private Date editedDate;

}
