package com.example.tenthread.redis;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenLogoutHandler implements LogoutHandler {
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    public TokenLogoutHandler(RedisUtil redisUtil, ObjectMapper objectMapper, JwtUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = extractTokenFromRequest(request);
        boolean isValidToken = jwtUtil.validateToken(token);

        if (!isValidToken) {
            // 유효하지 않은 토큰인 경우
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponseDto responseDto = new ApiResponseDto("유효하지 않은 요청입니다.", HttpStatus.BAD_REQUEST.value());
            return;
        }

        // 로그아웃 성공 코드
        response.setStatus(HttpServletResponse.SC_OK);
        ApiResponseDto responseDto = new ApiResponseDto("로그아웃 성공", HttpStatus.OK.value());

        // Redis 블랙리스트에 토큰 추가
        redisUtil.setBlackList(token, true);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
