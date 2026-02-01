package com.sushi.userprofile.cache;

import com.sushi.userprofile.model.User;

public class CacheResult {
    private final User user;
    private final boolean hit;
    private final boolean negative;

    private CacheResult(User user, boolean hit, boolean negative) {
        this.user = user;
        this.hit = hit;
        this.negative = negative;
    }

    public static CacheResult hit(User user) {
        return new CacheResult(user, true, false);
    }

    public static CacheResult miss() {
        return new CacheResult(null, false, false);
    }

    public static CacheResult negative() {
        return new CacheResult(null, false, true);
    }

    public User getUser() {
        return user;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isNegative() {
        return negative;
    }
}
