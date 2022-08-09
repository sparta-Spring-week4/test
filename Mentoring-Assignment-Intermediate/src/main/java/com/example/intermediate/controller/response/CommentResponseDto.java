package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.intermediate.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private String author;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  // 대댓글
  private Long parentId;
  private List<CommentResponseDto> children = new ArrayList<>();

  // 루트댓글의 null처리를 위함.
  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  /* Entity -> Dto*/
  public CommentResponseDto(Comment comment) {
    this.id = comment.getId();
    this.author = comment.getMember().getNickname();
    this.content = comment.getContent();
    this.createdAt = comment.getCreatedAt();
    this.modifiedAt = comment.getModifiedAt();
  }

}
