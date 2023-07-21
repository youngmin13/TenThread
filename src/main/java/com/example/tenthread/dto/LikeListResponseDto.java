package com.example.tenthread.dto;

import com.example.tenthread.entity.CommentLike;
import com.example.tenthread.entity.PostLike;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LikeListResponseDto {
    private List<Long> postIds;
    private List<Long> commentIds;
}