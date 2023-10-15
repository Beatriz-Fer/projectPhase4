package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.RetweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetweetRepository extends JpaRepository<RetweetEntity, Long> {

    // finds retweets made by user
    List<RetweetEntity> findByUser_Id(Long userId);

    // finds retweet by tweetId
    RetweetEntity findByTweet_Id(Long tweetId);


}
