package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.ProfilePasswordCheckDto;
import com.example.tenthread.dto.ProfileRequestDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
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
}
