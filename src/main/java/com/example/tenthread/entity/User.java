package com.example.tenthread.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    private boolean isBlocked;

    @Column(nullable = true)
    private String socialId;

    @Column(nullable = true)
    private String social;

    @OneToMany(mappedBy = "follow", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Follow> followList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Post> postList = new ArrayList<>();

    public User(String username, String password, String nickname, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isBlocked = false;
    }

    public User(String username, String password, String nickname, UserRoleEnum role, String socialId, String social) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.socialId = socialId;
        this.social = social;
        this.isBlocked = false;
    }

    public void setNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setBlocked(boolean flag){
        this.isBlocked = flag;
    }
    public User socialUpdate(String socialId, String social) {
        this.socialId = socialId;
        this.social = social;
        return this;
    }

    public void updateRole() {
        this.role = UserRoleEnum.ADMIN;
    }
}
