package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.*;
import com.example.tenthread.repository.CommentLikeRepository;
import com.example.tenthread.repository.CommentRepository;
import com.example.tenthread.repository.PostRepository;
import com.example.tenthread.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

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

    @Transactional
    public void likeComment(Long commentId, User user) {
        Comment comment = findComment(commentId);

        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            throw new DuplicateRequestException("이미 좋아요 한 댓글 입니다.");
        } else if (comment.getUser().getId().equals(user.getId())) {
            throw new RejectedExecutionException("자신이 남긴 댓글에는 좋아요를 누를 수 없습니다.");
        }else {
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);
        }
    }

    @Transactional
    public void deleteLikeComment(Long commentId, User user) {
        Comment comment = findComment(commentId);
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLikeOptional.isPresent()) {
            commentLikeRepository.delete(commentLikeOptional.get());
        } else {
            throw new IllegalArgumentException("해당 댓글에 취소할 좋아요가 없습니다.");
        }
    }

    public Comment findComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("선택한 댓글은 존재하지 않습니다.")
        );
    }
}
