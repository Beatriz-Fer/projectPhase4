package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    //gets all comments from a tweet
    List<CommentEntity> findByTweet_Id(Long tweetId);
}
