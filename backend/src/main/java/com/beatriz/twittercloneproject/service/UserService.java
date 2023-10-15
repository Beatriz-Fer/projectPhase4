package com.beatriz.twittercloneproject.service;

import com.beatriz.twittercloneproject.dto.*;
import com.beatriz.twittercloneproject.entity.*;
import com.beatriz.twittercloneproject.repository.LikeRepository;
import com.beatriz.twittercloneproject.repository.RetweetRepository;
import com.beatriz.twittercloneproject.repository.TweetRepository;
import com.beatriz.twittercloneproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TweetRepository tweetRepository;

    private final LikeRepository likeRepository;

    private final RetweetRepository retweetRepository;


    // create user when reigistering
    public UserDto getUser(Long id) {
        UserEntity user = this.userRepository.findById(id).orElseThrow();
        UserDto userDto = UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .bio(user.getBio())
                .profilePic(user.getProfilePic())
                .build();
        return userDto;
    }

    // update user profile
    public UserDto updateUser(Long id, UserDto user) {
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setProfilePic(user.getProfilePic());
        userEntity.setBio(user.getBio());
        this.userRepository.save(userEntity);
        return user;
    }

    // get user tweets
    @Transactional(readOnly = true)
    public List<TweetResponseDto> getUserTweets(Long id) {
        List<TweetResponseDto> userTweets = this.tweetRepository.findByUser_Id(id).stream()
                .map(this::mapTweetToDto)
                .collect(Collectors.toList());
        return userTweets;
    }
    // get user retweets
    @Transactional(readOnly = true)
    public List<TweetResponseDto> getUserRetweets(Long id) {
        List<TweetResponseDto> userTweets = this.retweetRepository.findByUser_Id(id).stream()
                .map(this::mapRetweetToDto)
                .collect(Collectors.toList());
        return userTweets;
    }
    // get user liked tweets
    @Transactional(readOnly = true)
    public List<TweetResponseDto> getUserLikedTweets(Long id) {
        List<TweetResponseDto> userTweets = this.likeRepository.findByUser_Id(id).stream()
                .map(this::mapLikeTweetToDto)
                .collect(Collectors.toList());
        return userTweets;
    }

    // get all users
    @Transactional (readOnly = true)
    public List<UserDto> getUsers() {
        List<UserDto> users = this.userRepository.findAll().stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
        return users;
    }


    ////// MAPPINGS //////

    // for tweets
    private TweetResponseDto mapTweetToDto(TweetEntity entity){
        UserEntity user = entity.getUser();
        TweetResponseDto tweetResponseDto = TweetResponseDto.builder()
                .id(entity.getId())
                .profilePic(user.getProfilePic())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .tweetText(entity.getText())
                .commentCounter(entity.getCommentCounter())
                .comments(entity.getComments().stream().map(this::mapCommentToDto).collect(Collectors.toList()))
                .retweetCounter(entity.getRetweetCounter())
                .likeCounter(entity.getLikeCounter())
                .isLiked(entity.getIsLiked())
                .createdDate(entity.getCreatedDate())
                .build();
        return tweetResponseDto;
    }

    // for retweets
    private TweetResponseDto mapRetweetToDto(RetweetEntity entity){
        TweetResponseDto tweetResponseDto = TweetResponseDto.builder()
                .id(entity.getId())
                .profilePic(entity.getUser().getProfilePic())
                .firstName(entity.getUser().getFirstName())
                .lastName(entity.getUser().getLastName())
                .username(entity.getUser().getUsername())
                .tweetText(entity.getTweet().getText())
                .commentCounter(entity.getTweet().getCommentCounter())
                .comments(entity.getTweet().getComments().stream().map(this::mapCommentToDto).collect(Collectors.toList()))
                .retweetCounter(entity.getRetweetCounter())
                .likeCounter(entity.getLikeCounter())
                .isLiked(entity.getIsLiked())
                .isRetweeted(entity.getIsRetweeted())
                .retweetCounter(entity.getRetweetCounter())
                .createdDate(entity.getCreatedDate())
                .build();
        return tweetResponseDto;
    }

    // for liked tweets
    private TweetResponseDto mapLikeTweetToDto(LikeEntity entity){
        if (entity.getRetweet() != null) {
            TweetResponseDto tweetResponseDto = TweetResponseDto.builder()
                    .id(entity.getId())
                    .profilePic(entity.getRetweet().getUser().getProfilePic())
                    .firstName(entity.getRetweet().getUser().getFirstName())
                    .lastName(entity.getRetweet().getUser().getLastName())
                    .username(entity.getRetweet().getUser().getUsername())
                    .tweetText(entity.getRetweet().getTweet().getText())
                    .commentCounter(entity.getRetweet().getTweet().getCommentCounter())
                    .comments(entity.getRetweet().getTweet().getComments().stream().map(this::mapCommentToDto).collect(Collectors.toList()))
                    .retweetCounter(entity.getRetweet().getTweet().getRetweetCounter())
                    .likeCounter(entity.getRetweet().getTweet().getLikeCounter())
                    .isLiked(entity.getRetweet().getTweet().getIsLiked())
                    .isRetweeted(entity.getRetweet().getIsRetweeted())
                    .retweetCounter(entity.getRetweet().getRetweetCounter())
                    .createdDate(entity.getRetweet().getCreatedDate())
                    .build();
            return tweetResponseDto;
        } else if (entity.getTweet() != null) {
            TweetResponseDto tweetResponseDto = TweetResponseDto.builder()
                    .id(entity.getId())
                    .profilePic(entity.getTweet().getUser().getProfilePic())
                    .firstName(entity.getTweet().getUser().getFirstName())
                    .lastName(entity.getTweet().getUser().getLastName())
                    .username(entity.getTweet().getUser().getUsername())
                    .tweetText(entity.getTweet().getText())
                    .commentCounter(entity.getTweet().getCommentCounter())
                    .comments(entity.getTweet().getComments().stream().map(this::mapCommentToDto).collect(Collectors.toList()))
                    .retweetCounter(entity.getTweet().getRetweetCounter())
                    .likeCounter(entity.getTweet().getLikeCounter())
                    .isLiked(entity.getTweet().getIsLiked())
                    .isRetweeted(entity.getTweet().getIsRetweeted())
                    .retweetCounter(entity.getTweet().getRetweetCounter())
                    .createdDate(entity.getTweet().getCreatedDate())
                    .build();
            return tweetResponseDto;
        }
        return null;
    }

    // for comments
    private CommentDto mapCommentToDto(CommentEntity commentEntity) {
        UserEntity commenter = commentEntity.getUser();
        CommentDto commentDto = CommentDto.builder()
                .id(commentEntity.getId())
                .tweetId(commentEntity.getTweet().getId())
                .userId(commenter.getId())
                .firstName(commenter.getFirstName())
                .lastName(commenter.getLastName())
                .username(commenter.getUsername())
                .text(commentEntity.getText())
                .createdDate(commentEntity.getCreatedDate())
                .build();
        return commentDto;
    }

    // for users
    private UserDto mapUserToDto(UserEntity user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .bio(user.getBio())
                .profilePic(user.getProfilePic())
                .build();
        return userDto;
    }


}
