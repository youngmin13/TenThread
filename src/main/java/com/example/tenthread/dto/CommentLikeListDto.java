package com.example.tenthread.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CommentLikeListDto {
    private List<CommentLikeDto> commentLikeList;

    public CommentLikeListDto(List<CommentLikeDto> commentLikeList) {
        this.commentLikeList = commentLikeList;
    }
}