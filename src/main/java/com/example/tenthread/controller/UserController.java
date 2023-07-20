package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.LoginRequestDto;
import com.example.tenthread.dto.UserRequestDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.KakaoService;
import com.example.tenthread.service.NaverService;
import com.example.tenthread.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        try {
            userService.login(requestDto, response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.CREATED.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout() {
        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return ResponseEntity.ok().body(new ApiResponseDto("카카오 로그인 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<ApiResponseDto> naverLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = naverService.naverLogin(code);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return ResponseEntity.ok().body(new ApiResponseDto("네이버 로그인 성공", HttpStatus.OK.value()));
    }
}
