package com.example.tenthread.repository;

import com.example.tenthread.entity.PrevPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrevPasswordRepository extends JpaRepository<PrevPassword, Long> {
    List<PrevPassword> findAllByUserIdOrderByCreatedAtDesc(Long used_id);
}
