package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetNickNameController {

    private final UserRepository userRepository;

    // SecurityContextHolder.getContext().getAuthentication().getName(); -> 유저 authId
    @GetMapping("/getMyNickName")
    public String isThereYourNickName() {
        String user_auth_id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserAuthId(user_auth_id);
        if (user != null) {
            user.setUserName("전문시청팀");
            userRepository.save(user);
        } else {
            user = new User(user_auth_id);
            userRepository.save(user);
        }
        return user.getUserName();
    }
}
