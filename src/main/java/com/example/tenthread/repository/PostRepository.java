package com.example.tenthread.repository;

import com.example.tenthread.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PostRepository extends JpaRepository<Post, Long> {
    Collection<Post> findAllByUserId(Long user_id);
}
