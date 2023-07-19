package com.example.tenthread.service;


import com.example.tenthread.dto.*;
import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import com.example.tenthread.entity.PostLike;
import com.example.tenthread.entity.User;
import com.example.tenthread.repository.PostLikeRepository;
import com.example.tenthread.repository.PostRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final S3Service s3Service;
    private final PostLikeRepository postLikeRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user, MultipartFile[] files) {

        validateFile(files);

        String fileNames = s3Service.uploadImage(files);

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        // 이미지 파일 처리
        postImageService.saveFile(fileNames, post);

        return new PostResponseDto(post);
    }
    public PostDetailResponseDto getPost(Long postId) {
        Post post = findPost(postId);

        return new PostDetailResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(PostRequestDto requestDto, Long postId, User user, MultipartFile[] files) throws IOException {

        validateFile(files);

        Post post = findPost(postId);

        validateUser(user, post);

        PostImage postImage = postImageService.getPostImage(postId);
        postImageService.updateFile(postImage,files);

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return new PostResponseDto(post);
    }

    public ApiResponseDto deletePost(Long postId, User user) throws IOException {
        Post post = findPost(postId);

        validateUser(user, post);
        PostImage postImage = postImageService.getPostImage(postId);
        String url = postImage.getFileName();
        s3Service.deleteImage(url.split(","));
        postRepository.delete(post);

        return new ApiResponseDto("삭제 성공", HttpStatus.OK.value());
    }

    @Transactional
    public void likePost(Long id, User user) {
        Post post = findPost(id);

        // if (postLikeRepository.findByUserAndPost(user, post).isPresent()) {
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new DuplicateRequestException("이미 좋아요 한 게시글 입니다.");
        } else {
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);
        }
    }

    @Transactional
    public void deleteLikePost(Long id, User user) {
        Post post = findPost(id);
        Optional<PostLike> postLikeOptional = postLikeRepository.findByUserAndPost(user, post);
        if (postLikeOptional.isPresent()) {
            postLikeRepository.delete(postLikeOptional.get());
        } else {
            throw new IllegalArgumentException("해당 게시글에 취소할 좋아요가 없습니다.");
        }
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

    public void validateFile(MultipartFile[] files) {
        if (files.length > 5) {
            throw new IllegalArgumentException("사진은 최대 5장까지 가능합니다.");
        }
    }

    public PostListResponseDto getPosts() {
        List<PostDetailResponseDto> postList = postRepository.findAll().stream()
                .map(PostDetailResponseDto::new)
                .collect(Collectors.toList());

        return new PostListResponseDto(postList);
    }
}
