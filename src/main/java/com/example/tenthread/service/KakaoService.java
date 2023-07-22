package com.example.tenthread.service;

import com.example.tenthread.dto.SocialUserInfoDto;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.redis.RedisUtil;
import com.example.tenthread.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.tenthread.entity.User;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public String[] kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String[] tokens = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(tokens[0]);

        // 3. 필요시에 회원 가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());

        // 5. 카카오 refreshToken 저장
        String kakaoRefreshToken = tokens[1];
        redisUtil.saveRefreshToken(kakaoUser.getUsername(), kakaoRefreshToken);

        return new String[]{createToken, kakaoRefreshToken};
    }

    private String[] getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "ae7708e065de7e86cbeadd427359aa6c");
        body.add("redirect_uri", "http://localhost:8080/api/auth/kakao/callback");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String[] res = new String[2];
        res[0] = jsonNode.get("access_token").asText();
        res[1] = jsonNode.get("refresh_token").asText();
        return res;
    }

    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String social = "KAKAO";

        // 실제 저장은 email이 아니라 username으로 되어있음
        return new SocialUserInfoDto(id, email, nickname, social);
    }

    private User registerKakaoUserIfNeeded(SocialUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String kakaoId = kakaoUserInfo.getId();
        String social = kakaoUserInfo.getSocial();
        User kakaoUser = userRepository.findBySocialIdAndSocial(kakaoId, social).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email (username) 동일한 email (username) 가진 회원이 있는지 확인
            String kakaoUsername = kakaoUserInfo.getUsername();
            User sameUsernameUser = userRepository.findByUsername(kakaoUsername).orElse(null);
            if (sameUsernameUser != null) {
                kakaoUser = sameUsernameUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.socialUpdate(kakaoId, social);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email -> username으로 하기로 했음 (kakao email <=> kakaoUserInfo username <=> User username)
                String email = kakaoUserInfo.getUsername();

                kakaoUser = new User(kakaoUserInfo.getNickname(), encodedPassword, email, UserRoleEnum.USER, kakaoId, social);
            }

            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}