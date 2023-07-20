package com.example.tenthread.service;

import com.example.tenthread.dto.SocialUserInfoDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j(topic = "NAVER Login")
@Service
@RequiredArgsConstructor
public class NaverService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    public String naverLogin(String code) throws JsonProcessingException {
        // 여기까지는 들어옴
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        SocialUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);

        // 3. 필요시에 회원 가입
        User kakaoUser = registerNaverUserIfNeeded(naverUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());

        return createToken;
    }

    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com")
                .path("/oauth2.0/token")
                .encode()
                .build()
                .toUri();
        //--------------//

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "XmNSzEFig_bVI5wTlG3V");
        body.add("client_secret", "StN6tgNiUr");
        body.add("redirect_uri", "http://localhost:8080/api/auth/naver/callback");
        body.add("code", code);
        body.add("state", "test");

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
        System.out.println("NaverService.getToken");
        System.out.println(jsonNode);
        return jsonNode.get("access_token").asText();
    }

    private SocialUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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
        String id = jsonNode.get("response").get("id").asText();
        String nickname = jsonNode.get("response")
                .get("nickname").asText();
        String email = jsonNode.get("response")
                .get("email").asText();
        String social = "NAVER";

        // 실제 저장은 email이 아니라 username으로 되어있음
        return new SocialUserInfoDto(id, email, nickname, social);
    }

    private User registerNaverUserIfNeeded(SocialUserInfoDto naverUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String naverId = naverUserInfo.getId();
        String social = naverUserInfo.getSocial();
        User naverUser = userRepository.findBySocialIdAndSocial(naverId, social).orElse(null);

        if (naverUser == null) {
            // 카카오 사용자 email (username) 동일한 email (username) 가진 회원이 있는지 확인
            String naverUsername = naverUserInfo.getUsername();
            User sameUsernameUser = userRepository.findByUsername(naverUsername).orElse(null);
            if (sameUsernameUser != null) {
                naverUser = sameUsernameUser;
                // 기존 회원정보에 카카오 Id 추가
                naverUser = naverUser.socialUpdate(naverId, social);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email -> username으로 하기로 했음 (kakao email <=> kakaoUserInfo username <=> User username)
                String email = naverUserInfo.getUsername();

                naverUser = new User(naverUserInfo.getNickname(), encodedPassword, email, UserRoleEnum.USER, naverId, social);
            }

            userRepository.save(naverUser);
        }
        return naverUser;
    }
}
