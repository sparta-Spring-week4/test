package com.example.intermediate.controller;

import com.example.intermediate.controller.request.HeartRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    // 좋아요 기능 API
    @PostMapping("/api/auth/heart/post/{postId}")
    public ResponseDto<?> saveHeart(@PathVariable Long postId,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.getPostHeart(postId, userDetails,request));
    }

    //좋아요 삭제 API
    @DeleteMapping("/heart")
    public ResponseDto<?> deleteHeart(@RequestBody HeartRequestDto heartRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.deleteHeart(heartRequestDto, userDetails, request));
    }
}
