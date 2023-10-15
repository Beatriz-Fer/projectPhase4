package com.beatriz.twittercloneproject.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonProperty("user_id")
    private UserEntity user;

    @JsonProperty("text")
    private String text;

    @JsonProperty("like_counter")
    private Integer likeCounter;

    @JsonProperty("isLiked")
    private Boolean isLiked;

    @JsonProperty("comment_counter")
    private Integer commentCounter;

    @JsonProperty("retweet_counter")
    private Integer retweetCounter;

    @JsonProperty("isRetweeted")
    private Boolean isRetweeted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tweet_id", referencedColumnName = "id")
    @JsonProperty("tweet_id")
    private TweetEntity tweet;

    @OneToMany(mappedBy = "tweet", fetch = FetchType.EAGER,  cascade = CascadeType.REMOVE)
    @JsonProperty("comments")
    private List<CommentEntity> comments;

    @JsonProperty("created_date")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "retweet_id", referencedColumnName = "id")
    @JsonProperty("retweetedBy")
    private RetweetEntity retweetedBy;


}
