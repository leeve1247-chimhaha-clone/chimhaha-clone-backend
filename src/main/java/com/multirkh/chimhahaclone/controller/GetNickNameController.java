package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetNickNameController {

    private final UserRepository userRepository;

    // SecurityContextHolder.getContext().getAuthentication().getName(); -> 유저 authId
    @GetMapping("/getMyNickName")
    public String isThereYourNickName() throws BadRequestException {
        User user = userRepository.findByUserAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user != null){
            user.setUserName("전문시청팀");
            userRepository.save(user);
        } else {
            throw new BadRequestException("There is no user");
        }
        return user.getUserName();
    }
}
