package com.ecommerce.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis Utility Class
 * Provides convenient methods for Redis operations
 */
@Component
public class RedisUtil {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Set key-value with expiration
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    /**
     * Set key-value without expiration
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    /**
     * Get value by key
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Delete key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * Check if key exists
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    /**
     * Set expiration for key
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    /**
     * Get expiration time in seconds
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    
    /**
     * Increment value (for counters)
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    /**
     * Decrement value (for counters)
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}


