package com.example.intermediate.service;

import com.example.intermediate.controller.request.HeartRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Heart;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.CommentCustomRepository;
import com.example.intermediate.repository.HeartRepository;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.util.CheckMemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final CommentCustomRepository commentCustomRepository;
    private final CheckMemberUtil checkMemberUtil;


    // 좋아요 등록
    @Transactional
    public ResponseDto<?> getPostHeart(HeartRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        //게시글 좋아요
        if(requestDto.getPostId() > 0 && requestDto.getCommentId() == null){
            Post post = checkMemberUtil.isPresentPost(requestDto.getPostId());
            Optional<Heart> postExists = heartRepository.findByPostAndMember(post, userDetails.getMember());
            if (postExists.isPresent()) {
                return ResponseDto.fail("NOT_FOUND", "이미 좋아요를 누른 게시글입니다.");
            }

            //게시글 존재 확인
            if (null == post) {
                return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
            }

            //게시글 좋아요 저장
            Heart heart = Heart.builder()
                    .post(post)
                    .member(userDetails.getMember())
                    .build();

            heartRepository.save(heart);

            // 게시글에 좋아요 수 보내기
            List<Heart> heartList = heartRepository.findByPostId(requestDto.getPostId());
            post.heartUpdate((long) heartList.size());

            return ResponseDto.success("게시글에 좋아요를 눌렀습니다.");
        }

        //댓글 좋아요
        if(requestDto.getPostId() != null && requestDto.getCommentId() != null){
            Comment comment = checkMemberUtil.isPresentComment(requestDto.getCommentId());
            Optional<Heart> commentExists = heartRepository.findByCommentAndMember(comment, userDetails.getMember());
            if (commentExists.isPresent()) {
                return ResponseDto.fail("NOT_FOUND", "이미 좋아요를 누른 댓글입니다.");
            }

            //댓글 존재 확인
            if (null == comment) {
                return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
            }

            //댓글 좋아요 저장
            Heart heart = Heart.builder()
                    .comment(comment)
                    .member(userDetails.getMember())
                    .build();

            heartRepository.save(heart);

            // 댓글에 좋아요 수 보내기
            List<Heart> commentHeartList = heartRepository.findByCommentId(requestDto.getCommentId());
            System.out.println("comment heart : " + commentHeartList.size());
            comment.commentHeartUpdate((long) commentHeartList.size());

            return ResponseDto.success("댓글에 좋아요를 눌렀습니다.");
        }


        return ResponseDto.success("좋아요를 눌렀습니다.");
    }


    // 좋아요 취소
    @Transactional
    public ResponseDto<?> deletePostHeart(HeartRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 게시글 좋아요 취소
        if(requestDto.getPostId() > 0 && requestDto.getCommentId() == null) {

            // 좋아요를 눌렀는지 확인
            Post post = checkMemberUtil.isPresentPost(requestDto.getPostId());
            Optional<Heart> postExists = heartRepository.findByPostAndMember(post, userDetails.getMember());
            if (postExists.isEmpty()) {
                return ResponseDto.fail("NOT_FOUND", "좋아요를 하지 않은 게시글입니다.");
            }

            //좋아요 삭제
            heartRepository.deleteByPostAndMember(post, userDetails.getMember());

            // 게시글에 좋아요 수 보내기
            List<Heart> heartList = heartRepository.findByPostId(requestDto.getPostId());
            post.heartUpdate((long) heartList.size());
        }

        // 댓글 좋아요 취소
        if(requestDto.getPostId() != null && requestDto.getCommentId() != null) {
            // 좋아요를 눌렀는지 확인
            Comment comment = checkMemberUtil.isPresentComment(requestDto.getCommentId());
            Optional<Heart> commentExists = heartRepository.findByCommentAndMember(comment, userDetails.getMember());
            if (commentExists.isEmpty()) {
                return ResponseDto.fail("NOT_FOUND", "좋아요를 하지 않은 게시글입니다.");
            }

            //좋아요 삭제
            heartRepository.deleteByCommentAndMember(comment, userDetails.getMember());

            // 게시글에 좋아요 수 보내기
            List<Heart> heartList = heartRepository.findByCommentId(requestDto.getCommentId());
            comment.commentHeartUpdate((long) heartList.size());
        }

        return ResponseDto.success("좋아요를 취소했습니다.");
    }
}