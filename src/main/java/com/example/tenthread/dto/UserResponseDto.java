package com.example.tenthread.dto;

import com.example.tenthread.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String username;
    private String nickname;

    public UserResponseDto(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }

}
