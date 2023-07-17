package com.example.tenthread.entity;

import com.example.tenthread.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends TimeStamped{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //댓글 내용
    @Column(nullable = false)
    private String body;

/*    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;*/

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;


    //댓글 작성 생성자
    public Comment(CommentRequestDto requestDto, Post post/*, User user*/) {
        this.body = requestDto.getBody();
        this.post = post;
        /*this.user = user;*/
    }

    //댓글 수정 메서드
    public void update(CommentRequestDto requestDto){
        this.body = requestDto.getBody();
    }

}
