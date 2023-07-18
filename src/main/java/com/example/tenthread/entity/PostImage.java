package com.example.tenthread.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "post_images")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private String fileName;

    public PostImage(String fileName, Post post) {
        this.fileName = fileName;
        this.post = post;
    }
}
