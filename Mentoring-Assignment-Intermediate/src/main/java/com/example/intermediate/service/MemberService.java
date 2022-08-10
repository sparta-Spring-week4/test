package com.example.intermediate.service;

import com.example.intermediate.controller.response.*;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.intermediate.repository.PostRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MemberService {
  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  MemberService(PostRepository postRepository, MemberRepository memberRepository, CommentRepository commentRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider){
    this.postRepository = postRepository;
    this.memberRepository = memberRepository;
    this.commentRepository = commentRepository;
    this. passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
  }

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME",
          "중복된 닉네임 입니다.");
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
          "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    Member member = Member.builder()
            .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    memberRepository.save(member);
    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getNickname());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

//    UsernamePasswordAuthenticationToken authenticationToken =
//        new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword());
//    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }

  @Transactional
  public ResponseDto<?> getAllMemberHist(UserDetailsImpl userDetails) {

    List<Post> myPostList = postRepository.findAllByMemberId(userDetails.getMember().getId()); // 빌더
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    for (Post post : myPostList) {
      postResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .imgUrl(post.getImgUrl())
                      .postHeartCount(post.getPostHeartCount())
                      .author(userDetails.getUsername())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }


    List<CommentResponseDto> myCommentList = commentRepository.findAllByMemberId(userDetails.getMember().getId());

    return ResponseDto.success(
            MemberHistResponseDto.builder()
                    .id(userDetails.getMember().getId())
                    .author(userDetails.getUsername())
                    .postList(postResponseDtoList)
                    .commentList(myCommentList)
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
