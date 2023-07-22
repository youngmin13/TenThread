package com.example.tenthread.redis;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

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
        String token = extractTokenFromCookie(request);

        // 블랙리스트에 등록된 토큰인지 확인
        boolean isBlacklistedToken = redisUtil.hasKeyBlackList(token);
        if (isBlacklistedToken) {
            return;
        }

        // 토큰의 유효성 검사
        boolean isValidToken = jwtUtil.validateToken(token);
        if (!isValidToken) {
            // 유효하지 않은 토큰인 경우
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponseDto responseDto = new ApiResponseDto("유효하지 않은 요청입니다.", HttpStatus.BAD_REQUEST.value());
            writeJsonResponse(response, responseDto);
            return;
        }

        // 토큰을 블랙리스트에 추가하여 로그아웃 처리
        redisUtil.setBlackList(token, true);

        // 로그아웃 성공적으로 처리되었음을 알리는 응답 반환
        response.setStatus(HttpServletResponse.SC_OK);
        ApiResponseDto responseDto = new ApiResponseDto("로그아웃 성공.", HttpStatus.OK.value());
        writeJsonResponse(response, responseDto);
    }


    private void writeJsonResponse(HttpServletResponse response, ApiResponseDto responseDto) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(responseDto));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            token = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("jwt")).findFirst().map(Cookie::getValue).orElse(null);
        }
        return token;
    }
}
