package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentLikeDto {
    private Long commentId;
    private boolean isLiked;
}