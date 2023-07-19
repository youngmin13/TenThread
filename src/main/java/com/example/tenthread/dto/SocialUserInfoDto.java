package com.example.tenthread.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserInfoDto {
    // email을 username으로 하기로 해서 변경
    private Long id;
    private String social;
    private String nickname;
    private String username;

    public SocialUserInfoDto(Long id, String nickname, String username, String social) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.social = social;
    }
}