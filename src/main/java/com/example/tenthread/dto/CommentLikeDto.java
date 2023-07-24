package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeDto {
    private Long commentId;
    private boolean isLiked;

    public CommentLikeDto(Long commentId, boolean isLiked) {
        this.commentId = commentId;
        this.isLiked = true;
    }
}
