package com.example.tenthread.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/back/user")
    public String adminMain(){
        return "backoffice/main";
    }

}
