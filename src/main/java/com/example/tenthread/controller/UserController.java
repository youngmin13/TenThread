package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.LoginRequestDto;
import com.example.tenthread.dto.UserRequestDto;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.service.KakaoService;
import com.example.tenthread.service.NaverService;
import com.example.tenthread.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;

    private final JwtUtil jwtUtil;

    public UserController(UserService userService, KakaoService kakaoService, NaverService naverService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.kakaoService = kakaoService;
        this.naverService = naverService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@Valid @RequestBody UserRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto("회원가입 성공", HttpStatus.CREATED.value()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        userService.login(requestDto, response);
        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.CREATED.value()));
    }

//    @PostMapping("/logout")
//    public void logout(HttpServletRequest request) {
//        userService.logout(request);
//    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String[] tokens = kakaoService.kakaoLogin(code, response);

        response.addHeader("Authorization", "Bearer " + tokens[0]);
        response.addHeader("Refresh_Token", tokens[1]);

        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.CREATED.value()));
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<ApiResponseDto> naverLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String[] tokens = naverService.naverLogin(code, response);

        response.addHeader("Authorization", "Bearer " + tokens[0]);
        response.addHeader("Refresh_Token", tokens[1]);

        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.CREATED.value()));
    }
}
