package com.example.tenthread.service;

import com.example.tenthread.dto.NoticeRequestDto;
import com.example.tenthread.dto.NoticeResponseDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.Notice;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.NoticeRepository;
import com.example.tenthread.repository.PostRepository;
import com.example.tenthread.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackOfficeService {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    //유저 전체 조회
    public List<UserResponseDto> getUserList(User user) {
        log.info("getUserList()");

        if(validateExistingUser(user)) {
            if (validateUserRole(user)) {
                return userRepository.findAllByOrderByIdDesc().stream().map(UserResponseDto::new).toList();
            } else {
                throw new IllegalArgumentException("관리자가 아닙니다.");
            }
        }
        return null;
    }

    //권한 수정하기 (일반 -> 관리자)
    @Transactional
    public void updateUserRole(Long userId, User user) {
        log.info("updateUserRole");
        if(validateExistingUser(user)) {
            if (validateUserRole(user)) {
                User changeRoleUser = userRepository.findById(userId).orElseThrow(
                        () -> new NullPointerException("관리자로 변환할 회원이 존재하지 않습니다.")
                );
                if (changeRoleUser.getRole().equals(UserRoleEnum.ADMIN)) {
                    throw new IllegalArgumentException("이미 관리자인 회원입니다.");
                } else if (changeRoleUser.getRole().equals(UserRoleEnum.USER)) {

                    changeRoleUser.updateRole();
                }
            } else {
                throw new IllegalArgumentException("관리자가 아닙니다.");
            }
        }
    }



    //공지글 작성
    public void createNotice(NoticeRequestDto requestDto, User user) {
        log.info("createNotice");
        if(validateExistingUser(user)) {
            if (validateUserRole(user)) {
                Notice notice = new Notice(requestDto, user);
                noticeRepository.save(notice);
            } else {
                throw new IllegalArgumentException("관리자가 아닙니다.");
            }
        }
    }

    //공지글 전체 조회
    public List<NoticeResponseDto> getNotices() {
        log.info("getNotices");
        return noticeRepository.findAllByOrderByModifiedAtDesc().stream().map(NoticeResponseDto::new).toList();

    }

    //공지글 한개 조회
    public NoticeResponseDto getNoticeOne(Long noticeId) {
        log.info("getNoticeOne");

        Notice foundNotice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new NullPointerException("공지글이 존재하지 않습니다.")
        );
        return new NoticeResponseDto(foundNotice);
    }


    //관리자 판별
    public boolean validateUserRole(User user){
        log.info("validateUserRole()");
        if(!user.getRole().equals(UserRoleEnum.ADMIN)){
            throw new NullPointerException("관리자가 아닙니다.");
        }else{
            return true;
        }
    }

    //존재하는 유저인지 판별
    public boolean validateExistingUser(User user){
        userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다."));

        return true;
    }

}
