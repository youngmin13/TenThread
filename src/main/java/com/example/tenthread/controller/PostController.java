package com.example.tenthread.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class PostController {
    private final PostService postService;

    private final AmazonS3 amazonS3Client;;

    @Autowired
    public PostController(AmazonS3 amazonS3Client, PostService postService) {
        this.amazonS3Client = amazonS3Client;
        this.postService = postService;
    }
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping("post/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PostMapping("post")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return postService.createPost(requestDto);
    }

    @PutMapping("post/{postId}")
    public PostResponseDto updatePost(@RequestBody PostRequestDto requestDto, @PathVariable Long postId) {
        return postService.updatePost(requestDto, postId);
    }

    @DeleteMapping("post/{postId}")
    public ApiResponseDto deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @PostMapping("/upload")
    public String createImage(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileUrl = "https://" + bucket + "/test/" + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
            return "Failed to upload the image.";
        }

        return fileUrl;
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
