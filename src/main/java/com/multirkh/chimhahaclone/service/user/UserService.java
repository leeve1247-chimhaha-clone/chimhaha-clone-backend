package com.multirkh.chimhahaclone.service.user;

import com.multirkh.chimhahaclone.entity.User;
import com.multirkh.chimhahaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String createUserInfo(String user_auth_id) {
        User user = userRepository.findByUserAuthId(user_auth_id);
        if (user == null) {
            user = new User(user_auth_id);
            user.setUserName("전문시청팀");
            userRepository.save(user);
        }
        return user.getUserName();
    }
}
