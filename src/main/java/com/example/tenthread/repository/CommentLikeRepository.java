package com.example.tenthread.repository;

import com.example.tenthread.entity.Comment;
import com.example.tenthread.entity.CommentLike;
import com.example.tenthread.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
    Boolean existsByUserAndComment(User user, Comment comment);
}