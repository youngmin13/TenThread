package com.example.tenthread.service;

import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackOfficeService {
    private final UserRepository userRepository;

    //유저 리스트 조회
    public List<UserResponseDto> getUserList(User user) {
        log.info("getUserList()");
        User existingUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new NullPointerException("해당 회원이 존재하지 않습니다.")
        );

        if(validateUserRole(existingUser)){
            return userRepository.findAllByOrderByIdDesc().stream().map(UserResponseDto::new).toList();
        }else{
            throw new NullPointerException("회원이 존재하지 않습니다.");
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
