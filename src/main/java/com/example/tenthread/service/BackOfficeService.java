package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackOfficeService {
    private final UserRepository userRepository;

    //유저 전체 조회
    public List<UserResponseDto> getUserList(User user) {
        log.info("getUserList()");
        User existingUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );
        if(validateUserRole(existingUser)){
            return userRepository.findAllByOrderByIdDesc().stream().map(UserResponseDto::new).toList();
        }else{
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }

    //권한 수정하기 (일반 -> 관리자)
    @Transactional
    public ApiResponseDto updateUserRole(Long userId, User user) {
        log.info("updateUserRole");
        User existingUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );

        if(validateUserRole(existingUser)) {
            User changeRoleUser = userRepository.findById(userId).orElseThrow(
                    () -> new NullPointerException("관리자로 변환할 회원이 존재하지 않습니다.")
            );
            if (changeRoleUser.getRole().equals(UserRoleEnum.ADMIN)) {
                throw new IllegalArgumentException("이미 관리자인 회원입니다.");
            } else if (changeRoleUser.getRole().equals(UserRoleEnum.USER)) {

                changeRoleUser.updateRole();
                return new ApiResponseDto("관리자 변환 성공", HttpStatus.OK.value());
            }
            return null;

        }else{
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
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
}
