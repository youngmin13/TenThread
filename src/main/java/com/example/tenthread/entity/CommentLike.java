package com.example.tenthread.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment_like")
@IdClass(CommentLikeId.class)
public class CommentLike {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    boolean isLiked;

    public CommentLike(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
        this.isLiked = true;
    }
}
