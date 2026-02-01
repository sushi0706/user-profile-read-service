package com.sushi.userprofile.cache;

import com.sushi.userprofile.model.User;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Component
public class InMemoryUserCache implements UserCache {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserCache.class);

    private static class CacheEntry {
        User user;
        long expiresAt;
        boolean notFound;

        CacheEntry(User user, long expiresAt,  boolean notFound) {
            this.user = user;
            this.expiresAt = expiresAt;
            this.notFound = notFound;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public CacheResult get(String id) {
        CacheEntry entry = cache.get(id);
        if(entry == null) {
            return CacheResult.miss();
        }
        if(System.currentTimeMillis() > entry.expiresAt) {
            cache.remove(id);
            return CacheResult.miss();
        }
        if(entry.notFound) {
            return CacheResult.negative();
        }
        return CacheResult.hit(entry.user);
    }

    @Override
    public void put(User user,  long ttlMillis) {
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        cache.put(user.getId(), new CacheEntry(user, expiresAt, false));
    }

    @Override
    public void invalidate(String id) {
        logger.debug("Cache Invalidate for userId={}", id);
        cache.remove(id);
    }

    @Override
    public void putNotFound(String id, long ttlMillis) {
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        cache.put(id, new CacheEntry(null, expiresAt, true));
    }
}
