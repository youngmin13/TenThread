package com.example.tenthread.service;

import com.example.tenthread.dto.*;
import com.example.tenthread.entity.PrevPassword;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.redis.RedisUtil;
import com.example.tenthread.repository.PrevPasswordRepository;
import com.example.tenthread.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    private final PrevPasswordRepository prevPasswordRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RedisUtil redisUtil, ObjectMapper objectMapper, JwtUtil jwtUtil, PrevPasswordRepository prevPasswordRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.prevPasswordRepository = prevPasswordRepository;
    }

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

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

        // prevPassword에 비밀번호 저장
        PrevPassword prevPassword = new PrevPassword(password, user);
        prevPasswordRepository.save(prevPassword);
    }

    public void login(LoginRequestDto requestDto, HttpServletResponse response) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        response.addHeader("Authorization", token);
    }

    public void logout(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        boolean isValidToken = jwtUtil.validateToken(token);

        if (isValidToken) {
            // Redis 블랙리스트에 토큰 추가
            redisUtil.setBlackList(token, true);
        }
    }


    public UserResponseDto getMyProfile(User user) {
        User myProfile = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("올바르지 않은 회원정보입니다.")
        );

        return new UserResponseDto(myProfile.getUsername(), myProfile.getNickname());
    }


    /**
     * 프로필 변경 메서드
     * @param user : 현재 로그인한 유저
     * @param profileRequestDto : 프로필 변경시 필요한 정보 (닉네임, 예전 비번, 바꿀 비번)
     */
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

        // 현재 유저 아이디를 가지고 있는 예전 비밀번호들 목록
        List<PrevPasswordResponseDto> prevPasswords = prevPasswordRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(PrevPasswordResponseDto::new).toList();

        for (PrevPasswordResponseDto pass : prevPasswords) {
            if (passwordEncoder.matches(newPassword, pass.getPassword())) {
                throw new IllegalArgumentException("최근 3번안에 사용한 비밀번호입니다.");
            }
        }

        if (prevPasswords.size() >= 3) {
            prevPasswords.remove(prevPasswords.size() - 1);
        }
        PrevPassword currentPassword = new PrevPassword(passwordEncoder.encode(newPassword), user);
        prevPasswordRepository.save(currentPassword);

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