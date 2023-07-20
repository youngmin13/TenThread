package com.example.tenthread.dto;

import com.example.tenthread.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private int likeCount;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getBody();
        this.nickname = comment.getUser().getNickname();
        this.likeCount = comment.getCommentLikes().size();
    }
}
