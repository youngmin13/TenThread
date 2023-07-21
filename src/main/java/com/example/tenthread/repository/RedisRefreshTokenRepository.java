package com.example.tenthread.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final long expirationTimeSeconds; // refreshToken 만료 시간

    public RedisRefreshTokenRepository(RedisTemplate<String, String> redisTemplate, long expirationTimeSeconds) {
        this.redisTemplate = redisTemplate;
        this.expirationTimeSeconds = expirationTimeSeconds;
    }

    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
        return refreshToken;
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return redisTemplate.hasKey(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public void saveRefreshToken(String refreshToken, String username) {
        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
    }

    public Optional<String> findByUsername(String username) {
        String refreshToken = redisTemplate.opsForValue().get(username);
        return Optional.ofNullable(refreshToken);
    }
}
