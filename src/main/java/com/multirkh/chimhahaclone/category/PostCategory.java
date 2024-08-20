package com.multirkh.chimhahaclone.category;

import com.multirkh.chimhahaclone.category.subCategory.HOBBY_CATEGORY;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "post_category",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "level"})}
)
@Getter

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
    private final Set<PostCategory> children = new HashSet<>();

    @Column(nullable = false)
    private Integer level;

    public PostCategory(MAJOR_CATEGORY majorCategory) {
        this.name = majorCategory.toString();
        this.level = 1;
    }

    public PostCategory(HOBBY_CATEGORY majorCategory, PostCategory parent) {
        this.name = majorCategory.toString();
        this.level = 2;
    }

    public PostCategory() {
    }
}