package com.example.tenthread.controller;

import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.LoginRequestDto;
import com.example.tenthread.dto.UserRequestDto;
import com.example.tenthread.service.KakaoService;
import com.example.tenthread.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;

    public UserController(UserService userService, JwtUtil jwtUtil, KakaoService kakaoService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.kakaoService = kakaoService;
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

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return ResponseEntity.ok().body(new ApiResponseDto("카카오 로그인 성공", HttpStatus.OK.value()));
    }
}
