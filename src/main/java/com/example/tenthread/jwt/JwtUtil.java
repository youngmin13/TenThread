package com.example.tenthread.jwt;

import com.example.tenthread.entity.RefreshToken;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.redis.RedisUtil;
import com.example.tenthread.repository.RefreshTokenRepository;
import com.example.tenthread.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;

import static org.hibernate.internal.CoreLogging.logger;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisUtil redisUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 키값. 사용자 권한도 토큰안에 넣어주기 때문에 그때 사용하는 키값
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken= request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }

    // Header에서 refreshToken 가져옴
    public String getRefreshTokenFromHeader(HttpServletRequest request, String type) {
        return request.getHeader(REFRESH_TOKEN);
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        // 토큰 만료시간
        long TOKEN_TIME = 60 * 60 * 1000L; // 60분
        String token = BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();

        return token;
    }

    public String createRefreshToken(String username) {
        Date date = new Date();
        long TOKEN_TIME = 2 * 60 * 1000L;
        return Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰의 위변조, 만료 체크
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            if(redisUtil.hasKeyBlackList(token)){
                logger.error("로그아웃하여 블랙리스트로 처리된 토큰입니다.");
                return false;
            }
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

    public boolean validateRefreshToken(String token) {
        // 1차 토큰 검증
        if (!validateToken(token)) return false;

        String username = getUsernameFromToken(token);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(getUsernameFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public UserRoleEnum getUserRoleFromToken(String token) {
        String username = getUsernameFromToken(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        return user.getRole();
    }
}