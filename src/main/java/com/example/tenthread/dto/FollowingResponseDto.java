package com.example.tenthread.dto;

import com.example.tenthread.entity.Follow;
import com.example.tenthread.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FollowingResponseDto {
    private Long id;
    private String nickname;
    private List<PostResponseDto> postList = new ArrayList<>();

    public FollowingResponseDto(Follow follow) {
        this.id = follow.getFollowing().getId();
        this.nickname = follow.getFollowing().getNickname();
        for(Post post : follow.getFollowing().getPostList()) {
            postList.add(new PostResponseDto(post));
        }
    }
}
