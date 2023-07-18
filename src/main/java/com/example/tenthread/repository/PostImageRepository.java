package com.example.tenthread.repository;

import com.example.tenthread.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByPostId(Long id);
}
