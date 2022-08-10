package com.example.intermediate.repository;

import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Post;
import static com.example.intermediate.domain.QComment.*;
import com.example.intermediate.domain.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentCustomRepository {

    private JPAQueryFactory jpaQueryFactory;

    public CommentCustomRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // 게시글의 댓글 전체 가져오기
    public List<Comment> findAllByPost(Post post){
        return jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .where(comment.post.id.eq(post.getId()))
                .orderBy(comment.parent.id.asc().nullsFirst(), comment.createdAt.asc())
                .fetch();
    }
}
