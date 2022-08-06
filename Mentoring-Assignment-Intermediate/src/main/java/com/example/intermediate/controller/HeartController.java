package com.example.intermediate.controller;

import com.example.intermediate.controller.request.HeartRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    // 좋아요 기능 API
    @PostMapping("/heart")
    public ResponseDto<?> saveHeart(@RequestBody HeartRequestDto heartRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.getHeart(heartRequestDto, userDetails, request));
    }

    //좋아요 삭제 API
    @DeleteMapping("/heart")
    public ResponseDto<?> deleteHeart(@RequestBody HeartRequestDto heartRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(heartService.deleteHeart(heartRequestDto, userDetails, request));
    }
}
