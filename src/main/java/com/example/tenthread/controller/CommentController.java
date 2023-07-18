package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/{postId}/comment")
    public ResponseEntity<ApiResponseDto> createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CommentRequestDto requestDto, @PathVariable Long postId){
      log.info("createComment : "+ requestDto.getBody());
      commentService.createComment(postId,requestDto,userDetails.getUser());
      return ResponseEntity.ok().body(new ApiResponseDto("댓글 작성 성공", HttpStatus.OK.value()));
    }

    //댓글 수정
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponseDto> updateComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CommentRequestDto requestDto, @PathVariable Long commentId){
        log.info("updateComment : "+ requestDto.getBody());
        commentService.updateComment(commentId,requestDto,userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("댓글 수정 성공", HttpStatus.OK.value()));
    }

    //댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponseDto> deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId){
        log.info("deleteComment : ");

        commentService.deleteComment(commentId,userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("댓글 삭제 성공", HttpStatus.OK.value()));
    }

    //예외 처리
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> handleException(NullPointerException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    //예외 처리
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
