package com.example.tenthread.controller;

import com.example.tenthread.dto.*;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<PostListResponseDto> getPosts() {
        PostListResponseDto result = postService.getPosts();
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDetailResponseDto result = postService.getPost(postId);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestParam("file") MultipartFile[] files, @RequestParam("postRequestDto") String requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        PostResponseDto result = postService.createPost(requestDto, userDetails.getUser(), files);
        return ResponseEntity.status(201).body(result);
    }


    @PutMapping("/post/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@RequestPart("postRequestDto") String requestDto,@RequestPart("file") MultipartFile[] files, @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        PostResponseDto result = postService.updatePost(requestDto, postId, userDetails.getUser(), files);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        ApiResponseDto result = postService.deletePost(postId, userDetails.getUser());
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/post/{postId}/like")
    public ResponseEntity<ApiResponseDto> likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.likePost(postId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("게시글 좋아요 성공", HttpStatus.ACCEPTED.value()));
    }

    @DeleteMapping("/post/{postId}/like")
    public ResponseEntity<ApiResponseDto> deleteLikePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deleteLikePost(postId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("게시글 좋아요 취소 성공", HttpStatus.ACCEPTED.value()));
    }

    @GetMapping("/myPosts")
    public ResponseEntity<PostListResponseDto> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostListResponseDto postListResponseDto = postService.getMyPosts(userDetails.getUser());
        return ResponseEntity.ok().body(postListResponseDto);
    }
}
