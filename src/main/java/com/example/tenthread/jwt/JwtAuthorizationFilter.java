package com.example.tenthread.jwt;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.getTokenFromHeader(request, "Access");
        String refreshToken = jwtUtil.getTokenFromHeader(request, "Refresh");

        if(accessToken != null) {
            if(jwtUtil.validateToken(accessToken)) {
                setAuthentication(jwtUtil.getUsernameFromToken(accessToken));
            } else if (refreshToken != null) {
                boolean isRefreshToken = jwtUtil.refreshTokenValidation(refreshToken);

                if(isRefreshToken) {
                    String loginId = jwtUtil.getUsernameFromToken(refreshToken);
                    String loginRole = jwtUtil.getRoleFromToken(refreshToken);
                    String newAccessToken = jwtUtil.createToken(loginId, UserRoleEnum.valueOf(loginRole), "Access");
                    jwtUtil.setAccessToken(response, newAccessToken);
                    setAuthentication(jwtUtil.getUsernameFromToken(newAccessToken));
                } else {
                    jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        Authentication authentication = jwtUtil.createAuthentication(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new ApiResponseDto(msg, status.value()));
            response.getWriter().write(json);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
