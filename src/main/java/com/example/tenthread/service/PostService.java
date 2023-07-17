package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(PostRequestDto requestDto, Long postId) {
        Post post = findPost(postId);

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return new PostResponseDto(post);
    }

    public ApiResponseDto deletePost(Long postId) {
        Post post = findPost(postId);

        postRepository.delete(post);

        return new ApiResponseDto("삭제 성공", HttpStatus.OK.value());
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");
        });
    }

    public PostResponseDto getPost(Long postId) {
        Post post = findPost(postId);

        return new PostResponseDto(post);
    }
}
