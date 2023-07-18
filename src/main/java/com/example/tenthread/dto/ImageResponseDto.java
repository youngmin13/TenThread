package com.example.tenthread.dto;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostImage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ImageResponseDto {
    private String image;

    public ImageResponseDto(PostImage image) {
        this.image = image.getFileName();
    }
}
