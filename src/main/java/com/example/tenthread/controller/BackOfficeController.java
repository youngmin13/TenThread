package com.example.tenthread.controller;

import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //2. 관리자 페이지로 이동

    //3. 권한 수정하기 (일반 -> 관리자)

}
