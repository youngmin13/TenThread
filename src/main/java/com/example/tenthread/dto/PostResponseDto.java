package com.example.tenthread.dto;

import com.example.tenthread.entity.Follow;
import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;
    private List<ImageResponseDto> imageList = new ArrayList<>();
    private Integer commentLen;
    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.likeCount = post.getPostLikes().size();
        if (post.getImages() != null) {
            for (PostImage image : post.getImages()) {
                imageList.add(new ImageResponseDto(image));
            }
        }
        this.commentLen = post.getComments().size();
    }
}
