package com.beatriz.twittercloneproject.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long userId;
    private Long tweetId;
    private String firstName;
    private String lastName;
    private String username;
    private String text;

    private Date createdDate;

}
