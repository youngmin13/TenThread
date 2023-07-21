package com.example.tenthread.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final long expirationTimeSeconds; // refreshToken 만료 시간

    public RedisRefreshTokenRepository(
            RedisTemplate<String, String> redisTemplate,
            @Value("${refresh.token.expiration.seconds}") long expirationTimeSeconds) {
        this.redisTemplate = redisTemplate;
        this.expirationTimeSeconds = expirationTimeSeconds;
    }

    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
        return refreshToken;
    }

    public String generateRefreshTokenInSocial(String refreshToken, String username) {
        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
        return refreshToken;
    }

    public Optional<String> findValidRefreshTokenByUsername(String username) {
        // Use keys() to get all the keys (refreshTokens) in Redis
        Set<String> refreshTokenKeys = redisTemplate.keys("*");

        // Loop through all the refreshTokens to find the one with the matching username
        for (String refreshToken : refreshTokenKeys) {
            // Get the refreshToken value (username) from Redis
            String storedUsername = redisTemplate.opsForValue().get(refreshToken);

            // Check if the storedUsername matches the provided username
            if (storedUsername != null && storedUsername.equals(username)) {
                // Check if the refreshToken is still valid (not expired)
                if (isRefreshTokenValid(refreshToken)) {
                    return Optional.ofNullable(refreshToken);
                } else {
                    // If the refreshToken is expired, remove it from Redis
                    deleteRefreshToken(refreshToken);
                }
            }
        }
        return Optional.empty();
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
