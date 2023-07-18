package com.example.tenthread.service;

import com.example.tenthread.dto.*;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public void signup(UserRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String getNickname = requestDto.getNickname();
        UserRoleEnum role = requestDto.getRole();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        User user = new User(username, password, getNickname, role);
        userRepository.save(user);
    }

    public void login(LoginRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        //사용자 확인 (username 이 없는 경우)
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        //비밀번호 확인 (password 가 다른 경우)
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    public UserResponseDto getMyProfile(User user) {
        User myProfile = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("올바르지 않은 회원정보입니다.")
        );

        return new UserResponseDto(myProfile.getUsername(), myProfile.getNickname());
    }


    public void updateProfile(User user, ProfileRequestDto profileRequestDto) {
        // 로그인한 유저가 존재하는지 한번 더 확인 -> 굳이 할 필요는 없을 듯...
        User updateProfile = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("올바르지 않은 회원정보입니다.")
        );

        // 바꿀 닉네임, 예전 비번, 바꿀 비번
        String newNickname = profileRequestDto.getNickname();
        String oldPassword = profileRequestDto.getOldPassword();
        String newPassword = profileRequestDto.getNewPassword();

        // 예전 비번이 현재 로그인한 유저의 비번이 맞는지
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 업데이트
        updateProfile.setNickname(newNickname);
        updateProfile.setPassword(passwordEncoder.encode(newPassword));

        // 저장
        userRepository.save(updateProfile);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        });
    }
}