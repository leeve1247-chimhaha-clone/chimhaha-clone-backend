package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.repository.UserRepository;
import com.multirkh.chimhahaclone.service.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    // SecurityContextHolder.getContext().getAuthentication().getName(); -> 유저 authId
    @RolesAllowed("USER")
    @GetMapping("/getMyNickName")
    public String isThereYourNickName() {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.createUserInfo(user_auth_id);
    }
}
