//package com.example.tenthread.controller;
//
//import com.example.tenthread.dto.RefreshTokenDto;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class RefreshTokenController {
//
//    private RedisTemplate<String, String> redisTemplate;
//
//    @PostMapping("/verify-refresh-token")
//    public ResponseEntity<String> verifyRefreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
//        String refreshToken = refreshTokenDto.getRefreshToken();
//
//        // Assuming you have some logic to get the user_id associated with the refreshToken
//        String userId = getUserIdFromRefreshToken(refreshToken);
//
//        // Retrieve the stored refresh token from Redis based on the user_id
//        String storedRefreshToken = redisTemplate.opsForValue().get("refresh_token:" + userId);
//
//        if (storedRefreshToken == null) {
//            return ResponseEntity.badRequest().body("Refresh token not found.");
//        }
//
//        // Compare the stored refresh token with the provided refresh token
//        if (refreshToken.equals(storedRefreshToken)) {
//            return ResponseEntity.ok("Refresh token is valid.");
//        } else {
//            return ResponseEntity.badRequest().body("Invalid refresh token.");
//        }
//    }
//
//    // Simulated method to get user_id associated with the refreshToken (You need to implement this logic)
//    private String getUserIdFromRefreshToken(String refreshToken) {
//        // Implement the logic to get user_id associated with the refreshToken
//        // For example, you might have a database query to find the user_id by refreshToken
//        // For this example, let's assume the user_id is stored in the refreshToken itself (not recommended in production)
//        // In a real-world scenario, you should decode the refreshToken and extract the user_id securely.
//        return refreshToken;
//    }
//}
