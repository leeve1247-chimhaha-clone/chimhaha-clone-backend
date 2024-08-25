package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.dto.PostDto;
import com.multirkh.chimhahaclone.entity.Post;
import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.PostRepository;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @GetMapping("/myAccount")
    public List<PostDto> getUserDetails(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username
    ){
        List<User> users;
        if (email != null){
            users = userRepository.findByEmail(email);
        } else if (username != null){
            users = userRepository.findByUserName(username);
        } else {
            return null;
        }

        if (users != null && !users.isEmpty()) {
            User user = users.getFirst();
            List<Post> postsByUser = postRepository.findByUser(user);
            if (postsByUser != null){
                return postsByUser.stream().map(PostDto::new).toList();
            }
        }
        return null;
    }

}
