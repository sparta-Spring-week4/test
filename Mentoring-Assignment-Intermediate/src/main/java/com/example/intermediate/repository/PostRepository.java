package com.example.intermediate.repository;

import com.example.intermediate.domain.Post;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();

  @Query("select p from Post p left join fetch Comment c On p.id = c.post.id where c is Null") // (1)
  List<Post> findEmptyPost();
}
