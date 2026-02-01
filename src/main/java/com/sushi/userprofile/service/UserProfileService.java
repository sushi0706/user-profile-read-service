package com.sushi.userprofile.service;

import com.sushi.userprofile.model.User;
import com.sushi.userprofile.cache.UserCache;
import com.sushi.userprofile.cache.CacheResult;
import com.sushi.userprofile.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserCache userCache;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    public UserProfileService(UserRepository userRepository,  UserCache userCache) {
        this.userCache = userCache;
        this.userRepository = userRepository;
    }

    public User getUser(String id) {
        // try cache
        CacheResult result = userCache.get(id);
        if(result.isHit()) {
            logger.debug("Cache HIT for userID={}",id);
            return result.getUser();
        }

        // check if negative cached
        if(result.isNegative()){
            logger.debug("Negative cache HIT for userID={}",id);
            throw new UserNotFoundException(id);
        }

        // fetch from repository
        logger.debug("Cache MISS for userId={}", id);
        User user = userRepository.findById(id);
        if (user == null) {
            userCache.putNotFound(id, 10_000);
            throw new UserNotFoundException(id);
        }

        // populate cache
        userCache.put(user, 60_000);
        logger.debug("Cache PUT for userId={}", id);

        return user;
    }

    public void updateUser(User user) {
        userRepository.update(user);
        userCache.invalidate(user.getId());
        logger.info("User updated and cache invalidated for userId={}", user.getId());
    }
}