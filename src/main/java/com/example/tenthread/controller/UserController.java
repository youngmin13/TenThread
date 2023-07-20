package com.example.tenthread.controller;

import com.example.tenthread.dto.*;
import com.example.tenthread.entity.CommentLike;
import com.example.tenthread.entity.PostLike;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.KakaoService;
import com.example.tenthread.service.NaverService;
import com.example.tenthread.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        userService.logout(request);
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

    @GetMapping("/likes")
    public ResponseEntity<LikeListResponseDto> getLikes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();

        List<Long> postIds = userService.getPostLikesByUserId(userId);
        List<Long> commentIds = userService.getCommentLikesByUserId(userId);

        LikeListResponseDto result = new LikeListResponseDto();
        result.setPostIds(postIds);
        result.setCommentIds(commentIds);

        return ResponseEntity.status(200).body(result);
    }
}
