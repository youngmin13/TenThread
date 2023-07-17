package com.example.tenthread.controller;

import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class PostController {
    private final PostService postService;

    @PostMapping("post")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return postService.createPost(requestDto);
    }
}
