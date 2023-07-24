package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLikeDto {
    private Long postId;
    private boolean isLiked;

    public PostLikeDto(Long postId, boolean isLiked) {
        this.postId = postId;
        this.isLiked = true;
    }
}