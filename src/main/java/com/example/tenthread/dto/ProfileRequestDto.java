package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequestDto {
    /**
     * 프로필 변경이 이루어질 때 필요한 정보
     */
    private String nickname;
    private String oldPassword;
    private String newPassword;
}
