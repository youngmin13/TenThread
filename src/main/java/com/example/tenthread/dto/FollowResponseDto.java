package com.example.tenthread.dto;

import com.example.tenthread.entity.Follow;
import com.example.tenthread.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FollowResponseDto {
    private Long id;
    private String nickname;
    private List<FollowingResponseDto> followingList = new ArrayList<>();

    public FollowResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();

        if (user.getFollowList() != null) {
            for (Follow following : user.getFollowList()) {
                followingList.add(new FollowingResponseDto(following));
            }
        }
    }
}

