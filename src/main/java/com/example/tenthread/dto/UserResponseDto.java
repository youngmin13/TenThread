package com.example.tenthread.dto;

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
}
