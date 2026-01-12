package com.sushi.userprofile.api;

import com.sushi.userprofile.model.User;
import com.sushi.userprofile.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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
}
