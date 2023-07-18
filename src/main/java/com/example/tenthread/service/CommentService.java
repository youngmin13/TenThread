package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.Comment;
import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.CommentRepository;
import com.example.tenthread.repository.PostRepository;
import com.example.tenthread.repository.UserRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void createComment(Long postId, CommentRequestDto requestDto, User user){

        userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        //댓글 작성
        Comment comment = new Comment(requestDto,post, user);
        commentRepository.save(comment);


    }


    @Transactional
    public void updateComment(Long commentId, CommentRequestDto requestDto, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );
        
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );
        
        if(user.getRole().equals(UserRoleEnum.ADMIN)){
            //댓글 수정 메서드
            comment.update(requestDto);
        } else{
            if(comment.getUser().getUsername().equals(user.getUsername())){
                comment.update(requestDto);
            }else{
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }
        }
    }


    public void deleteComment(Long commentId, User user) {
                userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        if(user.getRole().equals(UserRoleEnum.ADMIN)){
            //댓글 삭제 메서드
            commentRepository.delete(comment);
        }else {
            if (comment.getUser().getUsername().equals(user.getUsername())) {
                commentRepository.delete(comment);
            } else {
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
            }
        }

    }
}
