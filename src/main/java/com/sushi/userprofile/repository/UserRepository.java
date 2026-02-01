package com.sushi.userprofile.repository;

import com.sushi.userprofile.model.User;

public interface UserRepository {
    User findById(String id);
    void update(User user);
}
