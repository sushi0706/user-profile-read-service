package com.sushi.userprofile.service;

import com.sushi.userprofile.model.User;
import com.sushi.userprofile.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }
}