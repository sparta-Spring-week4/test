package com.example.intermediate.domain;

import com.example.intermediate.controller.request.HeartRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name="post_id")
    @ManyToOne
    private Post post;

    @JoinColumn(name="comment_id")
    @ManyToOne
    private Comment comment;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    @Builder.Default
    private Long postHeartCount = 0L;
}
