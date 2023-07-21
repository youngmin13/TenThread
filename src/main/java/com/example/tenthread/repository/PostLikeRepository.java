package com.example.tenthread.repository;

import com.example.tenthread.entity.Post;
import com.example.tenthread.entity.PostLike;
import com.example.tenthread.entity.PostLikeId;
import com.example.tenthread.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    Boolean existsByUserAndPost(User user, Post post);
    List<PostLike> findByUser(User user);
}