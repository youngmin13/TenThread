package com.example.tenthread.repository;

import com.example.tenthread.entity.Follow;
import com.example.tenthread.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowAndFollowing(User follow, User following);
}
