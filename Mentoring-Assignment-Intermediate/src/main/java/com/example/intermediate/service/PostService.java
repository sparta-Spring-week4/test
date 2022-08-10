package com.example.intermediate.service;

import com.example.intermediate.annotation.LoginCheck;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.intermediate.controller.response.CommentResponseDto;

import com.example.intermediate.controller.response.PostListResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.ImageRepository;
import com.example.intermediate.repository.PostRepository;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.util.CheckMemberUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ImageRepository imageRepository;

  private final TokenProvider tokenProvider;
  private final CheckMemberUtil checkMemberUtil;

  @Transactional
  @LoginCheck
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);

    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .member(member)
        .build();
    postRepository.save(post);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .author(post.getMember().getNickname())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .postHeartCount(post.getPostHeartCount())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {

    Post post = checkMemberUtil.isPresentPost(id);

    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    Map<Long, CommentResponseDto> map = new HashMap<>();

    commentList.stream().forEach(c -> {
              CommentResponseDto cdto = new CommentResponseDto(c);
              if(c.getParent() != null){
                cdto.setParentId(c.getParent().getId());
              }
              map.put(cdto.getId(), cdto);
              if (c.getParent() != null) map.get(c.getParent().getId()).getChildren().add(cdto);
              else commentResponseDtoList.add(cdto);
            }
    );

    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .commentResponseDtoList(commentResponseDtoList)
            .author(post.getMember().getNickname())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .postHeartCount(post.getPostHeartCount())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostListResponseDto> postResponseDtoList = new ArrayList<>();

    // 댓글 수
    List<Comment> commentList = new ArrayList<>();

    for(Post post : postList){
      List<Comment> postCommentList = post.getComments();
      for(Comment comment : postCommentList){
        if(comment.getParent() == null){
            commentList.add(comment);
        }
      }
    }

    // 게시글 목록 조회 response
    for(Post post : postList){
      postResponseDtoList.add(
        PostListResponseDto.builder()
              .id(post.getId())
              .title(post.getTitle())
              .author(post.getMember().getNickname())
              .postHeartCount(post.getPostHeartCount())
              .commentCount((long) commentList.size())
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success(postResponseDtoList);

  }

  @Transactional
  @LoginCheck
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);

    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = checkMemberUtil.isPresentPost(id);

    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  @LoginCheck
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {

    Member member = checkMemberUtil.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }


    Post post = checkMemberUtil.isPresentPost(id);

    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    System.out.println(post.getImgUrl());
    Image image = new Image(post.getImgUrl());

    imageRepository.save(image);
    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  //image upload
  @Transactional
  public ResponseDto<?> uploadFileV1(Long postId, MultipartFile multipartFile) throws IOException {
    validateFileExists(multipartFile);

    String fileName = CommonUtils.buildFileName(postId, multipartFile.getOriginalFilename());

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());


    try (InputStream inputStream = multipartFile.getInputStream()) {
      byte[] bytes = IOUtils.toByteArray(inputStream);
      objectMetadata.setContentLength(bytes.length);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
      amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, objectMetadata)
              .withCannedAcl(CannedAccessControlList.PublicReadWrite));
    } catch (IOException e) {
      throw new IOException("변환에 실패했습니다.");
    }
    Post post = postRepository.findById(postId).orElseThrow(
            () -> new NullPointerException("존재하지 않는 게시글입니다.")
    );
    String imgName = amazonS3Client.getUrl(bucketName, fileName).toString();
    post.updateImage(imgName);
    return ResponseDto.success(
            PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .author(post.getMember().getNickname())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .postHeartCount(post.getPostHeartCount())
            .build());
  }


  private void validateFileExists(MultipartFile multipartFile) throws IOException {
    if (multipartFile.isEmpty()) {
      throw new IOException();
    }
  }

}
