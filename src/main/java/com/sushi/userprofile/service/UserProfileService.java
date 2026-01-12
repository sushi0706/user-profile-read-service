package com.sushi.userprofile.service;

import com.sushi.userprofile.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    public User getUser(String id) {
        return new User(id, "User" + id);
    }
}
