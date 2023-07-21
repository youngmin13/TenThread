package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.CommentLikeResponseDto;
import com.example.tenthread.dto.CommentRequestDto;
import com.example.tenthread.dto.PostLikeResponseDto;
import com.example.tenthread.entity.Post;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.CommentService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/post/{postId}/comment")
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

    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<ApiResponseDto> likeComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.likeComment(commentId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("댓글 좋아요 성공", HttpStatus.ACCEPTED.value()));
    }

    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<ApiResponseDto> deleteLikeComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteLikeComment(commentId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("댓글 좋아요 취소 성공", HttpStatus.ACCEPTED.value()));
    }

    @GetMapping("/comment")
    public ResponseEntity<CommentLikeResponseDto> getCommentLikes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentLikeResponseDto result = commentService.getCommentLikes(userDetails.getUser());
        return ResponseEntity.status(200).body(result);
    }
}