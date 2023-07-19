package com.example.tenthread.dto;

import com.example.tenthread.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username;
    private int likeCount;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getBody();
        this.username = comment.getUser().getUsername();
        this.likeCount = comment.getCommentLikes().size();
    }
}
