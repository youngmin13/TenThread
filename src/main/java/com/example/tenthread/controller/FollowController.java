package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.FollowResponseDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("follow")
public class FollowController {

    private final FollowService followService;

    @GetMapping()
    public FollowResponseDto getFollow(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followService.getFollow(userDetails.getUser());
    }

    @PostMapping("/{userId}")
    public ApiResponseDto createFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followService.createFollow(userId, userDetails.getUser());
    }

    @DeleteMapping("/{userId}")
    public ApiResponseDto deleteFollow(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followService.deleteFollow(userId, userDetails.getUser());
    }
}
