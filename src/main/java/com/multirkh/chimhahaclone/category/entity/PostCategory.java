package com.multirkh.chimhahaclone.category.entity;

import com.multirkh.chimhahaclone.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "post_category"
)
@Getter
@NoArgsConstructor
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private String key;

    @Column(name = "korean")
    private String korean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Post> posts = new ArrayList<>();
}