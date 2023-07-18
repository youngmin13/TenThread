package com.example.tenthread.dto;

import com.example.tenthread.entity.PrevPassword;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PrevPasswordResponseDto {
    private Long id;
    private String password;
    private LocalDateTime createdAt;

    public PrevPasswordResponseDto (PrevPassword prevPassword) {
        this.password = prevPassword.getPassword();
        this.createdAt = prevPassword.getCreatedAt();
    }
}
