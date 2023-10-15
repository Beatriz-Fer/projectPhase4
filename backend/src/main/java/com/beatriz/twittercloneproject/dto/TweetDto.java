package com.beatriz.twittercloneproject.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetDto {
    private Long tweetId;
    private String text;


}
