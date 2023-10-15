package com.beatriz.twittercloneproject.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetweetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", referencedColumnName = "id")
    private TweetEntity tweet;

    @JsonProperty("like_counter")
    private Integer likeCounter;

    @JsonProperty("isLiked")
    private Boolean isLiked;

    @JsonProperty("retweet_counter")
    private Integer retweetCounter;

    @JsonProperty("isRetweeted")
    private Boolean isRetweeted;

    @JsonProperty("created_date")
    private Date createdDate;
}
