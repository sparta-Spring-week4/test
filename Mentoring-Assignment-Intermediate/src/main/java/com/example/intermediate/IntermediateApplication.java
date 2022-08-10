package com.example.intermediate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스프링 부트에서 스케줄러가 작동하게 합니다.
@EnableAspectJAutoProxy
@EnableJpaAuditing
@SpringBootApplication
public class IntermediateApplication {

  public static void main(String[] args) {
    SpringApplication.run(IntermediateApplication.class, args);
  }

}
