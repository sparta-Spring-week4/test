package com.example.intermediate.service;

import com.example.intermediate.annotation.LoginCheck;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentCustomRepository;
import com.example.intermediate.repository.CommentRepository;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.util.CheckMemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentCustomRepository commentCustomRepository;
  private final CheckMemberUtil checkMemberUtil;

  @Transactional
  @LoginCheck
  public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = checkMemberUtil.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment parent = null;
    // 자식댓글인 경우
    if(requestDto.getParentId() != null){
      parent = checkMemberUtil.isPresentComment(requestDto.getParentId());
      if (null == parent) {
        return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
      }
      // 부모댓글의 게시글 번호와 자식댓글의 게시글 번호 같은지 체크하기
      if(parent.getPost().getId() != requestDto.getPostId()){
        return ResponseDto.fail("NOT_FOUND", "부모댓글과 자식댓글의 게시글 번호가 일치하지 않습니다.");
      }
    }

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .content(requestDto.getContent())
        .build();
    // 자식댓글인경우 부모댓글을 설정해준다.
    if(null != parent){
      comment.updateParent(parent);
    }
    commentRepository.save(comment);

    CommentResponseDto commentResponseDto = null;
    if(parent != null){
      commentResponseDto = CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .parentId(comment.getParent().getId())
              .build();
    } else {
      commentResponseDto = CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build();
    }

    return ResponseDto.success(commentResponseDto);
  }

  // 댓글 조회
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = checkMemberUtil.isPresentPost(postId);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentCustomRepository.findAllByPost(post);

    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    Map<Long, CommentResponseDto> map = new HashMap<>();

    commentList.stream().forEach(c -> {
              CommentResponseDto cdto = new CommentResponseDto(c);
              /*if(c.getParent() != null){
                cdto.setParentId(c.getParent().getId());
              }*/
              map.put(cdto.getId(), cdto);
              if (c.getParent() != null) map.get(c.getParent().getId()).getChildren().add(cdto);
              else commentResponseDtoList.add(cdto);
            }
      );
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  @LoginCheck
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = checkMemberUtil.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment comment = checkMemberUtil.isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    comment.update(requestDto);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  @Transactional
  @LoginCheck
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = checkMemberUtil.isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }

}
