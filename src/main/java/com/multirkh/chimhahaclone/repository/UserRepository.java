package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserName(String username);
    List<User> findByEmail(String email);
}
