package com.beatriz.twittercloneproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", referencedColumnName = "id")
    private TweetEntity tweet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retweet_id", referencedColumnName = "id")
    private RetweetEntity retweet;


}
