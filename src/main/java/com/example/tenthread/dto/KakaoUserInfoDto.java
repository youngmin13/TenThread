package com.example.tenthread.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    // email을 username으로 하기로 해서 변경
    private Long id;
    private String nickname;
    private String username;

    public KakaoUserInfoDto(Long id, String nickname, String username) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
    }
}