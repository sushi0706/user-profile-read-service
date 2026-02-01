package com.sushi.userprofile.cache;

import com.sushi.userprofile.model.User;

public interface UserCache {
    CacheResult get(String id);
    void put(User user, long ttlMillis);
    void invalidate(String id);
    void putNotFound(String id, long ttlMillis);
}