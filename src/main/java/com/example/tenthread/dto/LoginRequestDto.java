package com.example.tenthread.dto;

import com.example.tenthread.entity.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private UserRoleEnum role;
}
