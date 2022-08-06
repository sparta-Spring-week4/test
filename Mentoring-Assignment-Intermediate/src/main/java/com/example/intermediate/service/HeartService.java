package com.example.intermediate.service;

import com.example.intermediate.controller.request.HeartRequestDto;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Heart;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.HeartRepository;
import com.example.intermediate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    // 좋아요 등록
    @Transactional
    public ResponseDto<?> getHeart(HeartRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 게시글 있는지 확인
        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        // 좋아요를 눌렀는지 확인
        Optional<Member> member = memberRepository.findById(requestDto.getMemberId());
        Optional<Heart> memberExists = heartRepository.findByMember(member.get());
        if (memberExists.isPresent()) {
            return ResponseDto.fail("NOT_FOUND", "좋아요를 누른 게시글입니다.");
        }

        //좋아요 저장
        Heart heart = Heart.builder()
                .post(post)
                .member(member.get())
                .build();

        heartRepository.save(heart);


        return ResponseDto.success("좋아요를 눌렀습니다.");
    }


    // 좋아요 취소
    @Transactional
    public ResponseDto<?> deleteHeart(HeartRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 게시글 있는지 확인
        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        // 좋아요를 눌렀는지 확인
        Optional<Member> member = memberRepository.findById(requestDto.getMemberId());
        Optional<Heart> memberExists = heartRepository.findByMember(member.get());
        if (memberExists.isEmpty()) {
            return ResponseDto.fail("NOT_FOUND", "좋아요를 누르지 않은 게시글입니다.");
        }

        //좋아요 삭제
        heartRepository.save(memberExists.get());


        return ResponseDto.success("좋아요를 취소했습니다.");
    }
}
