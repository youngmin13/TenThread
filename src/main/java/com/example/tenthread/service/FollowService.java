package com.example.tenthread.service;

import com.example.tenthread.dto.ApiResponseDto;
import com.example.tenthread.dto.FollowResponseDto;
import com.example.tenthread.entity.Follow;
import com.example.tenthread.entity.User;
import com.example.tenthread.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    public ApiResponseDto createFollow(Long userId, User user) {
        User followingUser = userService.getUser(userId);

        if (followingUser.getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("자시 자신은 팔로우 할 수 없습니다");
        }

        if (followRepository.findByFollowAndFollowing(user, followingUser).isPresent()) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = new Follow(user, followingUser);

        followRepository.save(follow);

        return new ApiResponseDto("팔로우 성공", HttpStatus.CREATED.value());
    }

    public ApiResponseDto deleteFollow(Long userId, User user) {
        User followingUser = userService.getUser(userId);

        Follow follow = followRepository.findByFollowAndFollowing(user, followingUser).orElseThrow(() -> {
            throw new IllegalArgumentException("팔로우 한 유저가 아닙니다!");
        });

        followRepository.delete(follow);

        return new ApiResponseDto("팔로우 취소", HttpStatus.OK.value());
    }

    public FollowResponseDto getFollow(User user) {
        return new FollowResponseDto(user);
    }
}
