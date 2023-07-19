package com.example.tenthread.service;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.repository.PostImageRepository;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;

    public void saveFile(String fileName, Post post) {
        PostImage postImage = new PostImage(fileName, post);

        postImageRepository.save(postImage);

        post.getImages().add(postImage);
        postRepository.save(post);
    }

    @Transactional
    public void updateFile(PostImage postImage, MultipartFile[] files) throws IOException {
        String[] oldImageList = fileArray(postImage.getFileName());

        String changeImage = s3Service.uploadImage(files);

        postImage.setFileName(changeImage);

        String[] changeImageList = fileArray(changeImage);

        if (!Arrays.equals(oldImageList, changeImageList)) {
            // changeImageList를 Set으로 변환하여 중복 제거
            Set<String> changeImageSet = new HashSet<>(Arrays.asList(changeImageList));

            // deleteImages 배열 구하기
            String[] deleteImages = Arrays.stream(oldImageList)
                    .filter(image -> !changeImageSet.contains(image))
                    .toArray(String[]::new);

            s3Service.deleteImage(deleteImages);
        }
    }

    public String[] fileArray(String urls) {
        String[] urlArray = urls.split(",");

        String[] fileNames = Arrays.stream(urlArray)
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .toArray(String[]::new);

        return fileNames;
    }

    public PostImage getPostImage(Long postId) {
        PostImage postImage = postImageRepository.findByPostId(postId).orElseThrow(() -> {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다");
        });
        return postImage;
    }
}
