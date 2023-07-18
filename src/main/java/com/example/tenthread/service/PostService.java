package com.example.tenthread.service;


import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.entity.User;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final S3Service s3Service;

    public PostResponseDto createPost(PostRequestDto requestDto, User user, MultipartFile[] files) {

        if (files.length > 5) {
            throw new IllegalArgumentException("사진은 최대 5장 까지 등록가능합니다!");
        }

        String fileNames = s3Service.uploadImage(files);

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        // 이미지 파일 처리
        postImageService.saveFile(fileNames, post);

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

    public ApiResponseDto deletePost(Long postId, User user) throws IOException {
        Post post = findPost(postId);

        validateUser(user, post);
        PostImage postImage = postImageService.getImage(postId);
        String url = postImage.getFileName();
        s3Service.deleteImage(url.split(","));
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
