package com.sushi.userprofile.api;

import com.sushi.userprofile.model.User;
import com.sushi.userprofile.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") String id) {
        return service.getUser(id);
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable("id") String id, @RequestBody User user) {
        if(!id.equals(user.getId())) {
            throw new IllegalArgumentException("Path Id and body id must match");
        }
        service.updateUser(user);
    }
}
