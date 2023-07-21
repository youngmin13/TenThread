package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostLikeDto {
    private Long postId;
    private boolean isLiked;
}
