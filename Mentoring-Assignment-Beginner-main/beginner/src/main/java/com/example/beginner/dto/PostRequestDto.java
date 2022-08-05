package com.example.beginner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
  private String title;
  private String content;
  private String author;
  private String password;

}
