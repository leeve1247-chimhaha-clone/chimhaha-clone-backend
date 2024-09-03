// src/main/java/com/multirkh/chimhahaclone/entity/User.java
package com.multirkh.chimhahaclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String userName;

    @Column(nullable = false)
    private String userAuthId;

    @OneToMany(mappedBy = "user") // mappedBy: 연관관계의 주인이 아님을 나타냄 (읽기 전용) DB 에선 안보임
    private List<Post> posts;

    public User(String userAuthId){
        this.userAuthId = userAuthId;
    }
}