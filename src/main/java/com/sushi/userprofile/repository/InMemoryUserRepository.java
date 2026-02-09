package com.sushi.userprofile.repository;

import com.sushi.userprofile.model.User;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

// @Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public InMemoryUserRepository() {
        users.put("1", new User("1", "Alice"));
        users.put("2", new User("2", "Bob"));
    }

    @Override
    public User findById(String id) {
        return users.get(id);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }
}
