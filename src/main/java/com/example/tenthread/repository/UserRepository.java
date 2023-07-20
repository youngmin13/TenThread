package com.example.tenthread.repository;

import com.example.tenthread.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

//    Optional<User> findByKakaoId(Long kakaoId);
    List<User> findAllByOrderByIdDesc();

    Optional<User> findBySocialIdAndSocial(String kakaoId, String social);
}
