package com.sushi.userprofile.service;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("Could not find user with id: " + userId);
    }
}
