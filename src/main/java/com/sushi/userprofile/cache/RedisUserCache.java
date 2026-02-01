package com.sushi.userprofile.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushi.userprofile.model.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUserCache implements UserCache {

    private static final String USER_KEY_PREFIX = "user:";
    private static final String NEGATIVE_PREFIX = ":neg";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper =  new ObjectMapper();

    public RedisUserCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CacheResult get(String id) {
        String userKey = USER_KEY_PREFIX + id;
        String negKey =  userKey + NEGATIVE_PREFIX;

        // positive cache
        String userJson = redisTemplate.opsForValue().get(userKey);
        if(userJson != null) {
            try {
                User user = objectMapper.readValue(userJson, User.class);
                return CacheResult.hit(user);
            } catch (Exception e) {
                redisTemplate.delete(userKey);
                return CacheResult.miss();
            }
        }

        // negative cache
        Boolean negativeExists = redisTemplate.hasKey(negKey);
        if(negativeExists) {
            return CacheResult.negative();
        }

        return CacheResult.miss();
    }

    @Override
    public void put(User user, long ttlMillis) {
        try {
            String userKey = USER_KEY_PREFIX + user.getId();
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(userKey, json, Duration.ofMillis(ttlMillis));
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void putNotFound(String id, long ttlMillis) {
        String negKey = USER_KEY_PREFIX + id +  NEGATIVE_PREFIX;
        redisTemplate.opsForValue().set(negKey, "1", Duration.ofMillis(ttlMillis));
    }

    @Override
    public void invalidate(String id){
        String baseKey =  USER_KEY_PREFIX + id;
        redisTemplate.delete(baseKey);
        redisTemplate.delete(baseKey +  NEGATIVE_PREFIX);
    }
}
