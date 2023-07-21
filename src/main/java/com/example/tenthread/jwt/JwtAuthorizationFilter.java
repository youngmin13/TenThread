package com.example.tenthread.jwt;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.RedisRefreshTokenRepository;
import com.example.tenthread.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;


    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper, RedisRefreshTokenRepository redisRefreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.redisRefreshTokenRepository = redisRefreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.resolveToken(request);

        if (accessToken != null) {
            // accessToken 유효
            if (jwtUtil.validateToken(accessToken)) {
                Claims info = jwtUtil.getUserInfoFromToken(accessToken);
                setAuthentication(info.getSubject());
            } else {
                // accessToken 만료되었으나 refreshToken 존재
                String username = jwtUtil.getUsernameFromToken(accessToken);
                if (username != null) {
                    Optional<String> validRefreshToken = redisRefreshTokenRepository.findValidRefreshTokenByUsername(username);

                    if (validRefreshToken.isPresent()) {
                        UserRoleEnum role = jwtUtil.getUserRoleFromToken(String.valueOf(validRefreshToken));
                        String newAccessToken = jwtUtil.createToken(username, role);
                        response.addHeader("Authorization", newAccessToken);
                        Claims info = jwtUtil.getUserInfoFromToken(newAccessToken);
                        setAuthentication(info.getSubject());
                    } else {
                        jwtExceptionHandler(response, "RefreshToken Expired or Invalid", HttpStatus.BAD_REQUEST);
                        return;
                    }
                } else {
                    jwtExceptionHandler(response, "AccessToken Invalid and Username Not Found", HttpStatus.BAD_REQUEST);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        // username -> user 조회 -> userDetails 에 담고 -> authentication의 principal 에 담고
        // -> securityContent 에 담고 -> SecurityContextHolder 에 담고
        // -> 이제 @AuthenticationPrincipal 로 조회할 수 있음
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Jwt 예외처리
    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new ApiResponseDto(msg, status.value()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}