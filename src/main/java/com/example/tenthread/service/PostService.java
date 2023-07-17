package com.example.tenthread.service;

import com.example.tenthread.dto.PostRequestDto;
import com.example.tenthread.dto.PostResponseDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);

        postRepository.save(post);

        return new PostResponseDto(post);
    }
}
