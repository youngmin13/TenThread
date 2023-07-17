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
import com.example.tenthread.entity.User;
import com.example.tenthread.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto, user);

        postRepository.save(post);

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
