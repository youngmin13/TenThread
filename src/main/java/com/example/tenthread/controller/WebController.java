package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/createPost")
    public String createPost() {
        return "createPost";
    }
    @GetMapping("/signup")
    public String signup() { return "signup"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout() {
        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", HttpStatus.OK.value()));
    }
    @GetMapping("/back/user")
    public String adminMain(){
        return "backoffice/main";
    }

}
