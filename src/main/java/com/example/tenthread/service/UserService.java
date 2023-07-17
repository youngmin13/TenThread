package com.example.tenthread.service;

import com.example.tenthread.dto.LoginRequestDto;
import com.example.tenthread.dto.ProfileRequestDto;
import com.example.tenthread.dto.UserRequestDto;
import com.example.tenthread.dto.UserResponseDto;
import com.example.tenthread.entity.User;
import com.example.tenthread.entity.UserRoleEnum;
import com.example.tenthread.jwt.JwtUtil;
import com.example.tenthread.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    public void beforeProfilePasswordCheck(User user, String password) {
        User beforeProfileUser = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("로그인해주세요.")
        );

        if(!passwordEncoder.matches(password, beforeProfileUser.getPassword())) {
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
        User updateProfile = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("올바르지 않은 회원정보입니다.")
        );

        String newNickname = profileRequestDto.getNickname();
        String newPassword = profileRequestDto.getPassword();

        updateProfile.setNickname(newNickname);
        updateProfile.setPassword(newPassword);

        userRepository.save(updateProfile);
    }
}