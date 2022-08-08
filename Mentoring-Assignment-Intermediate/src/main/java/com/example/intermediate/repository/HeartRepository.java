package com.example.intermediate.repository;

import com.example.intermediate.domain.Heart;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByPostAndMember(Post post, Member member);
    List<Heart> findByPostId(Long postId);
    void deleteByPostAndMember(Post post, Member member);


}
