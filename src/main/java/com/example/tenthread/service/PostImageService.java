package com.example.tenthread.service;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.repository.PostImageRepository;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;

    public void saveFile(String fileName, Post post) {
        PostImage postImage = new PostImage(fileName, post);

        postImageRepository.save(postImage);

        post.getImages().add(postImage);
        postRepository.save(post);
    }

    public PostImage getImage(Long postId) {
        PostImage postImage = postImageRepository.findByPostId(postId).orElseThrow(() -> {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다");
        });
        return postImage;
    }
}
