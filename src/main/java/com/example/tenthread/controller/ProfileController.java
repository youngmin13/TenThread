package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.ProfileRequestDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 나의 프로필 정보
     * @param userDetails : 현재 로그인한 유저
     * @return : username (email)이랑 nickname
     */
    @GetMapping("/profile")
    public UserResponseDto getMyProfile (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyProfile(userDetails.getUser());
    }

    /**
     * 프로필 업데이트
     * @param userDetails : 현재 로그인한 유저
     * @param profileRequestDto : 프로필 변경시 필요한 정보 (닉네임, 예전 비번, 바꿀 비번)
     * @return 프로필 변경 성공 or exception
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDto> updateProfile (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestBody ProfileRequestDto profileRequestDto) {
        userService.updateProfile(userDetails.getUser(), profileRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("프로필 변경 성공", HttpStatus.ACCEPTED.value()));
    }
}
