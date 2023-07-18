package com.example.tenthread.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follow_id")
    private User follow;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    public Follow(User follow, User following) {
        this.follow = follow;
        this.following = following;
    }
}
