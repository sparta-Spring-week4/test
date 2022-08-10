package com.example.intermediate.domain;

import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final PostRepository postRepository;


    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 * * * * *")
    public void deleteFile(){
        System.out.println("안쓰는 게시물 삭제");
        List<Post> posts = postRepository.findEmptyPost();
        System.out.println("진행되는중");
        for( Post post : posts){
            System.out.println(post.getId()+"번 게시글을 삭제합니다.");
            postRepository.delete(post);
        }
    }



}
