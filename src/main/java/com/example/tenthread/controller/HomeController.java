package com.example.tenthread.controller;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.UserRepository;
import com.example.tenthread.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "profile";
    }

    @GetMapping("/signup")
    public String signup() { return "signup"; }

    @GetMapping("/login")
    public String login() { return "login"; }
}