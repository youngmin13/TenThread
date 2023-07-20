package com.example.tenthread.repository;

import com.example.tenthread.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
    Boolean existsByUserAndComment(User user, Comment comment);
    @Query("SELECT pl.comment.id FROM CommentLike pl WHERE pl.user.id = :userId")
    List<Long> findCommentIdsByUserId(@Param("userId") Long userId);
}