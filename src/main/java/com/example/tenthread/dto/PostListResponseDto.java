package com.example.tenthread.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponseDto {
    private List<PostDetailResponseDto> postsList;
    public PostListResponseDto(List<PostDetailResponseDto> postList) {
        this.postsList = postList;
    }
}
