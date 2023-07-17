package com.example.tenthread.service;

import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.Comment;
import com.example.tenthread.repository.CommentRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public void createComment(CommentRequestDto requestDto, User user){

        userRepository.findById(user.getId()).orElseThrow(
                ()-> new NullPointerException("해당 회원이 존재하지 않습니다."));

        Comment comment = new Comment(requestDto);
        commentRepository.save(comment);
    }


}
