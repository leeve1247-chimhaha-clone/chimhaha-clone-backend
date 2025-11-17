package com.multirkh.chimhahaclone.api.post.category.domain;

import com.multirkh.chimhahaclone.api.post.domain.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "post_category"
)
@Getter
@NoArgsConstructor
public class PostCategory {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostCategory> children = new ArrayList<>();
    @OneToMany(mappedBy = "category")
    private final List<Post> posts = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer level;
    @Column(nullable = false, name = "`key`")
    private String key;
    @Column(name = "korean")
    private String korean;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostCategory parent;
}