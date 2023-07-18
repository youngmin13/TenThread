package com.example.tenthread.service;

/*import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.catalina.filters.AddDefaultCharsetFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
*/
import com.example.tenthread.dto.*;
import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.entity.User;
import com.example.tenthread.repository.PostImageRepository;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;

    public PostResponseDto createPost(PostRequestDto requestDto, User user, MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();

        if (files.length > 5) {
            throw new IllegalArgumentException("사진은 최대 5장 까지 등록가능합니다!");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            // 파일 처리 로직
            fileNames.add(fileName);
        }

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        // 이미지 파일 처리
        for (String fileName : fileNames) {
            postImageService.saveFile(fileName, post);
        }

        return new PostResponseDto(post);
    }
    public PostResponseDto getPost(Long postId) {
        Post post = findPost(postId);

        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(PostRequestDto requestDto, Long postId, User user) {
        Post post = findPost(postId);

        validateUser(user, post);

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return new PostResponseDto(post);
    }

    public ApiResponseDto deletePost(Long postId, User user) {
        Post post = findPost(postId);

        validateUser(user, post);

        postRepository.delete(post);

        return new ApiResponseDto("삭제 성공", HttpStatus.OK.value());
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");
        });
    }

    public boolean validateUser(User user, Post post) {
        if (!post.getUser().getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("게시글을 작성한 유저가 아닙니다.");
        } else {
            return true;
        }
    }
}
