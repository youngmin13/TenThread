package com.example.tenthread.controller;

import com.example.tenthread.dto.*;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.PostService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("posts")
    public ResponseEntity<PostListResponseDto> getPosts() {
        PostListResponseDto result = postService.getPosts();
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("post/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDetailResponseDto result = postService.getPost(postId);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("post")
    public ResponseEntity<PostResponseDto> createPost(@RequestPart("file") MultipartFile[] files, @RequestPart("PostRequestDto") PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDto result = postService.createPost(requestDto, userDetails.getUser(), files);
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("post/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@RequestPart("PostRequestDto") PostRequestDto requestDto,@RequestPart("file") MultipartFile[] files, @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        PostResponseDto result = postService.updatePost(requestDto, postId, userDetails.getUser(), files);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("post/{postId}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        ApiResponseDto result = postService.deletePost(postId, userDetails.getUser());
        return ResponseEntity.status(200).body(result);
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
