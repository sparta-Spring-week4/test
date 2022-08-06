package com.example.intermediate.domain;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.intermediate.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final ImageRepository imageRepository;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteFile() throws InterruptedException {
        System.out.println("이미지 삭제 실행");
        // 삭제할 이미지 조회
        List<Image> images = imageRepository.findAll();
        for (Image img : images) {
            DeleteObjectRequest request = new DeleteObjectRequest(bucketName, img.getImgUrl());
            amazonS3Client.deleteObject(request);
            imageRepository.delete(img);
        }
    }

}
