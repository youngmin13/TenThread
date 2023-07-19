package com.example.tenthread.redis;

import com.example.tenthread.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtLogoutHandler implements LogoutHandler {
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    public JwtLogoutHandler(RedisUtil redisUtil, JwtUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = extractTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            System.out.println("유효하지 않은 요청입니다.");;
        }

        if (jwtUtil.validateToken(token)) {
            int expirationMinutes = 30;
            redisUtil.setBlackList(token, true, expirationMinutes);
        } else {
            System.out.println("유효하지 않은 요청입니다.");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
