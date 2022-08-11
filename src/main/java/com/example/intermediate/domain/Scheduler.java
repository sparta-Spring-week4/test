package com.example.intermediate.domain;

import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final PostRepository postRepository;


    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 * * * *")
    public void deleteFile(){
        List<Post> posts = postRepository.findEmptyPost();
        for( Post post : posts){
            postRepository.delete(post);
            log.info("게시물 <" + post.getTitle() + ">이 삭제됬습니다.");
        }
    }



}
