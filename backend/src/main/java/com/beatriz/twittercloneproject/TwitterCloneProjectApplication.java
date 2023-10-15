package com.beatriz.twittercloneproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TwitterCloneProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterCloneProjectApplication.class, args);
    }

}
