package com.example.beginner.repository;

import com.example.beginner.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findById(Long id);
  List<Post> findAllByOrderByModifiedAtDesc();

}
