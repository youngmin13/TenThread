package com.example.tenthread.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponseDto {
    private List<PostDetailResponseDto> postList;
    public PostListResponseDto(List<PostDetailResponseDto> postList) {
        this.postList = postList;
    }
}
