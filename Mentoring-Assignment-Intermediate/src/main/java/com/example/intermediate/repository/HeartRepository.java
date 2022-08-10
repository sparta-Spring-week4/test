package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Heart;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    // 게시글에 달린 좋아요
    Optional<Heart> findByPostAndMember(Post post, Member member);
    List<Heart> findByPostId(Long postId);
    void deleteByPostAndMember(Post post, Member member);

    // 댓글에 달린 좋아요
    Optional<Heart> findByCommentAndMember(Comment comment, Member member);
    List<Heart> findByCommentId(Long commentId);
    void deleteByCommentAndMember(Comment comment, Member member);

}
