package com.example.tenthread.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("post/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId);
    }

    @PostMapping("post")
    public PostResponseDto createPost(@RequestPart("file") MultipartFile[] files, @RequestPart("PostRequestDto") PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails.getUser(), files);
    }

    @PutMapping("post/{postId}")
    public PostResponseDto updatePost(@RequestBody PostRequestDto requestDto, @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(requestDto, postId, userDetails.getUser());
    }

    @DeleteMapping("post/{postId}")
    public ApiResponseDto deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.deletePost(postId, userDetails.getUser());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
