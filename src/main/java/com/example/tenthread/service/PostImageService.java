package com.example.tenthread.service;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final PostImageRepository postImageRepository;

    public void saveFile(String fileName, Post post) {
        PostImage postImage = new PostImage(fileName, post);

        postImageRepository.save(postImage);
    }
}
