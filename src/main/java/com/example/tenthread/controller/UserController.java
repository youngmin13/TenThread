package com.example.tenthread.controller;

import com.example.tenthread.dto.*;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.UserService;
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

    public UserController(UserService userService) {
        this.userService = userService;
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
        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/profile")
    public UserResponseDto getMyProfile (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyProfile(userDetails.getUser());
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDto> updateProfile (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestBody ProfileRequestDto profileRequestDto) {
        userService.updateProfile(userDetails.getUser(), profileRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("프로필 변경 성공", HttpStatus.ACCEPTED.value()));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout() {
        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", HttpStatus.OK.value()));
    }
}
