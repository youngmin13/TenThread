package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.Comment;
import com.example.tenthread.entity.Post;
import com.example.tenthread.repository.CommentRepository;
import com.example.tenthread.repository.PostRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    /*private final UserRepository userRepository;*/
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public ApiResponseDto createComment(Long postId, CommentRequestDto requestDto/*, User user*/){

        /*userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );
*/
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        //댓글 작성
        Comment comment = new Comment(requestDto,post);
        commentRepository.save(comment);

        return new ApiResponseDto("댓글 작성 성공", HttpStatus.OK.value());
    }


}
