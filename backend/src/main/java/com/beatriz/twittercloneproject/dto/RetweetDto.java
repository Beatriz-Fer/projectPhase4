package com.beatriz.twittercloneproject.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetweetDto {
    private Long id;
    private String profilePic;
    private String firstName;
    private String lastName;
    private String username;
    private Integer likeCounter;
    private Boolean isLiked;
    private Integer retweetCounter;
    private Boolean isRetweeted;

    private Date createdDate;

}

