package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentLikeResponseDto {
    private List<CommentLikeDto> commentLikeList;
    public CommentLikeResponseDto(List<CommentLikeDto> commentLikeList) {
        this.commentLikeList = commentLikeList;
    }
}