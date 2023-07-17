package com.example.tenthread.controller;

import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/comment/{postId}")
    public void createComment(/*@AuthenticationPrincipal UserDetailsImpl userDetails,*/ @RequestBody CommentRequestDto requestDto, @PathVariable Long postId){
      log.info("createComment : "+ requestDto.getBody());
      commentService.createComment(postId,requestDto/*,userDetails.getUser()*/);
    }

}
