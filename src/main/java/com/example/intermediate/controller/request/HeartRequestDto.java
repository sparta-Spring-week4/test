package com.example.intermediate.controller.request;

import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HeartRequestDto {
    private Long postId;
    private Long commentId;
}
