package com.example.tenthread.redis;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    // Header KEY 값
    public static final String REFRESH_TOKEN_HEADER = "Refresh_Token";

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${refresh.token.expiration.seconds}") // Expiration time for refreshToken in seconds
    private long expirationTimeSeconds;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate,
                     RedisTemplate<String, Object> redisBlackListTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisBlackListTemplate = redisBlackListTemplate;
    }

    // 랜덤한 refreshToken을 생성하는 메서드
    public String generateRandomRefreshToken() {
        // Generate random bytes
        byte[] randomBytes = new byte[16];
        new Random().nextBytes(randomBytes);

        // Convert random bytes to a Base64 string
        String refreshToken = Base64.getEncoder().encodeToString(randomBytes);
        return refreshToken;
    }

    // 특정 username에 대한 refreshToken을 생성하고 Redis에 저장하는 메서드
    public String generateAndSetRefreshTokenForUser(String username) {
        String refreshToken = generateRandomRefreshToken();
        set(refreshToken, username, (int) (expirationTimeSeconds / 60)); // Convert to minutes
        return refreshToken;
    }

    public void set(String key, Object o, int minutes) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        redisTemplate.opsForValue().set(key, o, minutes, TimeUnit.MINUTES);
    }

    public Optional<String> findValidRefreshTokenByUsername(String username) {
        // Use keys() to get all the keys (refreshTokens) in Redis
        Set<String> refreshTokenKeys = redisTemplate.keys("*");

        // Loop through all the refreshTokens to find the one with the matching username
        for (String refreshToken : refreshTokenKeys) {
            // Get the refreshToken value (username) from Redis
            String storedUsername = (String) redisTemplate.opsForValue().get(refreshToken); // Cast to String

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
        Object refreshTokenObject = redisTemplate.opsForValue().get(username);
        if (refreshTokenObject instanceof String) {
            String refreshToken = (String) refreshTokenObject;
            return Optional.ofNullable(refreshToken);
        }
        return Optional.empty();
    }


    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 토큰을 블랙리스트에 추가하는 메서드
    public void setBlackList(String token, boolean isValid) {
        // 토큰을 블랙리스트에 추가하고 만료 시간 설정 (예: 1시간)
        redisBlackListTemplate.opsForValue().set(token, isValid, 1, TimeUnit.HOURS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

}
