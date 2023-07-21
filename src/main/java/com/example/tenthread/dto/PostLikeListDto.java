package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostLikeListDto {
    private List<PostLikeDto> postLikeList;

    public PostLikeListDto(List<PostLikeDto> postLikeList) {
        this.postLikeList = postLikeList;
    }
}