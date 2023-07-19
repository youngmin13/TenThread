package com.example.tenthread.controller;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.NoticeRequestDto;
import com.example.tenthread.dto.NoticeResponseDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.security.UserDetailsImpl;
import com.example.tenthread.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BackOfficeController {

    private final BackOfficeService backOfficeService;

    //1. 유저 전체 조회 (최신 가입순)
    @GetMapping("/back/user")
    public List<UserResponseDto> getUserList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return backOfficeService.getUserList(userDetails.getUser());
    }

    //2. 권한 수정하기 (일반 -> 관리자)
    @PatchMapping("/back/user/{userId}")
    public ResponseEntity<ApiResponseDto> updateUserRole(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        backOfficeService.updateUserRole(userId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("관리자로 변경 성공", HttpStatus.ACCEPTED.value()));
    }

    //3. 공지글 등록
    @PostMapping("/back/notice")
    public ResponseEntity<ApiResponseDto> createNotice(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody NoticeRequestDto requestDto){
        backOfficeService.createNotice(requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("공지글 등록 완료", HttpStatus.CREATED.value()));
    }

    //4. 공지글 전체 조회
    @GetMapping("/back/notice")
    public List<NoticeResponseDto> getNotices(){
        return backOfficeService.getNotices();

    }
    //5. 공지글 한개 조회
    @GetMapping("/back/notice/{noticeId}")
    public NoticeResponseDto getNoticeOne(@PathVariable Long noticeId){
        return backOfficeService.getNoticeOne(noticeId);

    }

    //6. 공지글 수정
    @PutMapping("/back/notice/{noticeId}")
    public ResponseEntity<ApiResponseDto> updateNotice(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long noticeId ,@RequestBody NoticeRequestDto requestDto){
        backOfficeService.updateNotice(noticeId,requestDto,userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("공지글 수정 완료",HttpStatus.OK.value()));
    }

    //7. 공지글 삭제
    @DeleteMapping("/back/notice/{noticeId}")
    public ResponseEntity<ApiResponseDto> deleteNotice(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long noticeId){
        backOfficeService.deleteNotice(noticeId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("공지글 삭제 완료",HttpStatus.OK.value()));
    }

    //8. 유저 차단하기
    @PutMapping("/back/user/{userId}/block")
    public ResponseEntity<ApiResponseDto> blockUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId){
        backOfficeService.blockUser(userId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("유저 차단 완료",HttpStatus.OK.value()));
    }

    //9. 유저 차단 해제 하기
    @PutMapping("/back/user/{userId}/unblock")
    public ResponseEntity<ApiResponseDto> unBlockUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        backOfficeService.unBlockUser(userId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("유저 차단 해제 완료", HttpStatus.OK.value()));
    }


    //예외 처리 nullPointerException
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> handleException(NullPointerException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    //예외 처리 IllegalArgumentException
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        ApiResponseDto restApiException = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}