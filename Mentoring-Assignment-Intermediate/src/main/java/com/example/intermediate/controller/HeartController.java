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
    @PostMapping("/api/auth/heart")
    public ResponseDto<?> saveHeart(@RequestBody HeartRequestDto requestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.getPostHeart(requestDto, userDetails,request));
    }

    //좋아요 삭제 API
    @DeleteMapping("/api/auth/heart")
    public ResponseDto<?> deleteHeart(@RequestBody HeartRequestDto requestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.deletePostHeart(requestDto, userDetails, request));
    }
}
