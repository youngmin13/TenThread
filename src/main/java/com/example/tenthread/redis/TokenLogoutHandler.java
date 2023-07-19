package com.example.tenthread.redis;

import com.example.tenthread.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenLogoutHandler implements LogoutHandler {
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    public TokenLogoutHandler(RedisUtil redisUtil, JwtUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String token = extractTokenFromRequest(request);
            boolean isValidToken = jwtUtil.validateToken(token);

            if (!isValidToken) {
                return;
            }

            redisUtil.setBlackList(token, true);

        } catch(Exception e) {
            e.printStackTrace();
            return;
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
