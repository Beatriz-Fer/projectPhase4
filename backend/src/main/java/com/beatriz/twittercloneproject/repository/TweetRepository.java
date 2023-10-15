package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.TweetEntity;
import com.beatriz.twittercloneproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<TweetEntity, Long> {

    // finds tweets that are liked
    List<TweetEntity> findByIsLiked(boolean isLiked);

    // finds tweets that are retweeted
    List<TweetEntity> findByIsRetweeted(boolean isRetweeted);

    // finds tweets that are made by user
    List<TweetEntity> findByUser_Id(Long userId);

    // finds tweets that exist by text and user
    boolean existsByTextAndUser(String text, UserEntity user);


}
