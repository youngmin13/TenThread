package com.example.tenthread.jwt;

import com.example.tenthread.dto.TokenDto;
import com.example.tenthread.entity.RefreshToken;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.RefreshTokenRepository;
import com.example.tenthread.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String AUTHORIZATION_KEY = "role";
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    private static final long ACCESS_TIME = 15 * 60 * 1000L; // 15분
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto createAllToken(String username, UserRoleEnum role) {
        return new TokenDto(createToken(username, role, "Access"), createToken(username, role, "Refresh"));
    }

    public String createToken(String username, UserRoleEnum role, String type) {
        Date date = new Date();

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + time)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String getTokenFromHeader(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public Boolean refreshTokenValidation(String token) {
        if(!validateToken(token)) return false;

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(getUsernameFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    public Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get(AUTHORIZATION_KEY, String.class);
    }

    public void setAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Access_Token", accessToken);
    }

    public void setRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh_Token", refreshToken);
    }


//    public Claims getUserInfoFromToken(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//    }
}
