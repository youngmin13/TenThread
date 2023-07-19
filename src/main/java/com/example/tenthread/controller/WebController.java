package com.example.tenthread.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

}
