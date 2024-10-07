package com.multirkh.chimhahaclone.category.entity;

import com.multirkh.chimhahaclone.category.MAJOR_CATEGORY;
import com.multirkh.chimhahaclone.category.subCategory.HOBBY_CATEGORY;
import com.multirkh.chimhahaclone.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "post_category",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "level"})}
)
@Getter
@NoArgsConstructor
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Post> posts = new ArrayList<>();

    @Column(nullable = false)
    private Integer level;

    public PostCategory(MAJOR_CATEGORY majorCategory) {
        this.name = majorCategory.toString();
        this.level = 1;
    }

    public PostCategory(HOBBY_CATEGORY majorCategory, PostCategory parent) {
        this.name = majorCategory.toString();
        this.parent = parent;
        this.level = 2;
    }
}