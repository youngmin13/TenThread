package com.example.tenthread.controller;

import com.example.tenthread.dto.ProfilePasswordCheckDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileWebController {
    private final UserService userService;

    public ProfileWebController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/beforeProfile")
    public String beforeProfilePasswordCheck (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @RequestBody ProfilePasswordCheckDto profilePasswordCheckDto) {
        userService.beforeProfilePasswordCheck(userDetails.getUser(), profilePasswordCheckDto);
        return "redirect:/profile";
    }
}
