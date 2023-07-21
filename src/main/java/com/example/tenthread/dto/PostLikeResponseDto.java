package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostLikeResponseDto {
    private List<PostLikeDto> postLikeList;
    public PostLikeResponseDto(List<PostLikeDto> postLikeList) {
        this.postLikeList = postLikeList;
    }
}
