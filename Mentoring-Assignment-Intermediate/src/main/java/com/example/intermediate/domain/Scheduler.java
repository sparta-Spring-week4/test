package com.example.intermediate.domain;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.intermediate.repository.ImageRepository;
import com.example.intermediate.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final PostService postService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final ImageRepository imageRepository;


    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 * * * * *")
    public void deleteFile(){
        System.out.println("이미지 삭제 실행");
        // 삭제할 이미지 조회
        List<Image> images = imageRepository.findAll();
        for (Image img : images) {
            String key = extract(img.getImgUrl());
            System.out.println(key);

            imageRepository.delete(img);
        }
    }

    public String extract(String url){
        String head = "https://magorosc.s3.ap-northeast-2.amazonaws.com/";
        return url.substring(head.length());
    }

}
