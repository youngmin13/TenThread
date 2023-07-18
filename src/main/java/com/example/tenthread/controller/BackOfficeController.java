package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BackOfficeController {

    private final BackOfficeService backOfficeService;

    //1. 유저 전체 조회 (최신 가입순)
    @GetMapping("/back/users")
    public List<UserResponseDto> getUserList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return backOfficeService.getUserList(userDetails.getUser());
    }

    //2. 권한 수정하기 (일반 -> 관리자)
    @PatchMapping("/back/{userId}")
    public ResponseEntity<ApiResponseDto> updateUserRole(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        backOfficeService.updateUserRole(userId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("관리자로 변경 성공", HttpStatus.ACCEPTED.value()));
    }

    //예외 처리 nullPointerException
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> handleException(NullPointerException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    //예외 처리 IllegalArgumentException
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
