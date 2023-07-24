package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/main")
public class WebController {

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/createPost")
    public String createPost() {
        return "createPost";
    }

    @GetMapping("/updatePost")
    public String updatePost(Model model, @RequestParam("postId") int postId,@RequestParam("title") String title, @RequestParam("content") String content) {
        model.addAttribute("postId", postId);
        model.addAttribute("title", title);
        model.addAttribute("content", content);

        return "updatePost";
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

    @GetMapping("/back/notice")
    public String adminNotice(){
        return "backoffice/notice";
    }
    @GetMapping("/back/notice/create")
    public String createNotice(){
        return "backoffice/createNotice";
    }

    @GetMapping("/back/notice/{noticeId}")
    public String adminNoticeOne(@PathVariable Long noticeId, Model model){
        model.addAttribute("noticeId",noticeId);
        return "backoffice/noticeOne";
    }

    @GetMapping("/back/notice/{noticeId}/view")
    public String NoticeView(@PathVariable Long noticeId, Model model){
        model.addAttribute("noticeId",noticeId);
        return "backoffice/noticeView";
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "profile";
    }

    @GetMapping("/myPage")
    public String myPage() {
        return "myPage";
    }

    @GetMapping("/myFollow")
    public String myFollow() {
        return "myFollow";
    }
}
