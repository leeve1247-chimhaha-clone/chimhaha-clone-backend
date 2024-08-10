package com.multirkh.chimhahaclone.repository;

import com.multirkh.chimhahaclone.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
    }

    @Test
    void testReadUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        savedUser.setPassword("newpassword");
        User updatedUser = userRepository.save(savedUser);

        Optional<User> foundUser = userRepository.findById(updatedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newpassword", foundUser.get().getPassword());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }
}