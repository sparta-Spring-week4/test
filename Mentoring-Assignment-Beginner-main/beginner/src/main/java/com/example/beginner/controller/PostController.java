package com.example.beginner.controller;

import com.example.beginner.dto.PostRequestDto;
import com.example.beginner.dto.ResponseDto;
import com.example.beginner.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController { 

  private final PostService postService;

  @PostMapping("/api/post")
  public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto) {
    return postService.createPost(requestDto);
  }

  @GetMapping("/api/post/{id}")
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  @GetMapping("/api/post")
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  @PutMapping("/api/post/{id}")
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto) {
    return postService.updatePost(id, postRequestDto);
  }

  @DeleteMapping("/api/post/{id}")
  public ResponseDto<?> deletePost(@PathVariable Long id) {
    return postService.deletePost(id);
  }

  @PostMapping("/api/post/{id}")
  public ResponseDto<?> validateAuthorByPassword(@PathVariable Long id, @RequestBody String password) {
    return postService.validateAuthorByPassword(id, password);
  }

}
