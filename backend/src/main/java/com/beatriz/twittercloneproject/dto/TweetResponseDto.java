package com.beatriz.twittercloneproject.dto;

import com.beatriz.twittercloneproject.entity.UserEntity;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetResponseDto {
    private Long id;
    private String profilePic;
    private String firstName;
    private String lastName;
    private String username;
    private String tweetText;
    private Integer commentCounter;
    private List<Object> comments;
    private Integer retweetCounter;
    private Integer likeCounter;
    private Boolean isLiked;
    private RetweetDto retweetedBy;
    private Boolean isRetweeted;

    private Date createdDate;


}
