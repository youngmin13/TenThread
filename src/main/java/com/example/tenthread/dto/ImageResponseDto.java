package com.example.tenthread.dto;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ImageResponseDto {
    private Long id;
    private String image1;

    public ImageResponseDto(PostImage image) {
        this.id = image.getId();
        this.image1 = image.getImage1();
    }
}
