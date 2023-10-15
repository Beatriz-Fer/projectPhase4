package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.LikeEntity;
import com.beatriz.twittercloneproject.entity.RetweetEntity;
import com.beatriz.twittercloneproject.entity.TweetEntity;
import com.beatriz.twittercloneproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    // finds by user and tweet
    Optional<LikeEntity> findByUserAndTweet(UserEntity user, TweetEntity tweet);

    //gets all likes made by a user
    List<LikeEntity> findByUser_Id(Long userId);

    //gets all likes from a retweet
    Optional<LikeEntity> findByUserAndRetweet(UserEntity user, RetweetEntity retweet);

}
