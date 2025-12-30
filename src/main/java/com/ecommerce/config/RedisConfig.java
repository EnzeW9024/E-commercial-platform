package com.ecommerce.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Configuration
 * Configures Redis connection, serialization, and caching
 */
@Configuration
@EnableCaching
public class RedisConfig {
    
    /**
     * RedisTemplate Configuration
     * Used for direct Redis operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key serializer - String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value serializer - JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Cache Manager Configuration
     * Configures cache expiration and serialization
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Default TTL: 1 hour
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues(); // Don't cache null values
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("products", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("product", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("orders", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("order", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("users", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("user", 
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .build();
    }
}


