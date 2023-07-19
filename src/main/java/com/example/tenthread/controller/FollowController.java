package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.FollowResponseDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    @GetMapping()
    public ResponseEntity<FollowResponseDto> getFollow(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        FollowResponseDto result = followService.getFollow(userDetails.getUser());
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponseDto> createFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApiResponseDto result = followService.createFollow(userId, userDetails.getUser());
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDto> deleteFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApiResponseDto result = followService.deleteFollow(userId, userDetails.getUser());
        return ResponseEntity.status(200).body(result);
    }
}
