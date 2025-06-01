package com.multirkh.chimhahaclone.api.user;

import com.multirkh.chimhahaclone.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserAuthId(String userAuthId);
}
