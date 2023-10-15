package com.beatriz.twittercloneproject.service;

import com.beatriz.twittercloneproject.dto.*;
import com.beatriz.twittercloneproject.entity.*;
import com.beatriz.twittercloneproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetService {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final AuthService authService;
    private final RetweetRepository retweetRepository;


    // create a tweet
    @Transactional
    public void tweet(TweetDto tweetDto) {
        UserEntity user = authService.getUserFromJwt();

        // Check if the user has already tweeted the same text
        if (tweetDto.getTweetId() == null && tweetDto.getText() != null) {
            if (tweetRepository.existsByTextAndUser(tweetDto.getText(), user)) {
                System.out.println("You have already tweeted this text.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        if(tweetDto.getTweetId() != null){
            TweetEntity tweet = tweetRepository.findById(tweetDto.getTweetId()).orElseThrow();
            this.tweetRepository.save(tweet);
        } else {
            this.createTweet(user, tweetDto.getText());
        }
    }

   private void createTweet(UserEntity user, String text){
        this.tweetRepository.save(
                TweetEntity.builder()
                        .user(user)
                        .text(text)
                        .commentCounter(0)
                        .retweetCounter(0)
                        .likeCounter(0)
                        .createdDate(Date.from(Instant.now()))
                        .isLiked(false)
                        .isRetweeted(false)
                        .build()
        );
    }


    // like a tweet
    @Transactional
    public void like(Long id) {
        UserEntity user = authService.getUserFromJwt();

        // Check if it's a retweet or an original tweet
        Optional<TweetEntity> tweetOptional = tweetRepository.findById(id);
        Optional<RetweetEntity> retweetOptional = Optional.ofNullable(retweetRepository.findByTweet_Id(id));
        if (retweetOptional.isPresent()) {
            // It's a retweet
            TweetEntity tweet = tweetOptional.get();
            RetweetEntity retweet = retweetOptional.get();
            Optional<LikeEntity> optional2 = likeRepository.findByUserAndRetweet(user, retweet);
            if (optional2.isPresent()) {
                retweet.setLikeCounter(retweet.getLikeCounter() - 1);
                retweet.setIsLiked(false);
                tweet.getRetweetedBy().setIsLiked(false);
                tweet.getRetweetedBy().setLikeCounter(tweet.getRetweetedBy().getLikeCounter() - 1);
                this.tweetRepository.save(tweet);
                this.retweetRepository.save(retweet);
                this.likeRepository.delete(optional2.get());
            } else {
                retweet.setLikeCounter(retweet.getLikeCounter() + 1);
                retweet.setIsLiked(true);
                tweet.getRetweetedBy().setIsLiked(true);
                tweet.getRetweetedBy().setLikeCounter(tweet.getRetweetedBy().getLikeCounter() + 1);
                this.tweetRepository.save(tweet);
                this.retweetRepository.save(retweet);
                this.likeRepository.save(
                        LikeEntity.builder()
                                .user(user)
                                .retweet(retweet)
                                .build());
            }
        } else if (retweetOptional.isPresent() == false) {
            // It's an original tweet
            TweetEntity tweet = tweetOptional.get();
            Optional<LikeEntity> optional = likeRepository.findByUserAndTweet(user, tweet);
            if (optional.isPresent()) {
                tweet.setLikeCounter(tweet.getLikeCounter() - 1);
                tweet.setIsLiked(false);
                this.tweetRepository.save(tweet);
                this.likeRepository.delete(optional.get());
            } else if (tweetOptional.isPresent()) {
                tweet.setLikeCounter(tweet.getLikeCounter() + 1);
                tweet.setIsLiked(true);
                this.tweetRepository.save(tweet);
                this.likeRepository.save(
                        LikeEntity.builder()
                                .user(user)
                                .tweet(tweet)
                                .build());
            }
        } else {
            throw new RuntimeException("Tweet or retweet with ID " + id + " not found");
        }
    }


    @Transactional (readOnly = true)
    public List<TweetResponseDto> isLiked() {
       return tweetRepository.findByIsLiked(true).stream()
                .map(this::mapTweetToDto)
                .collect(Collectors.toList());
    }


    //Comment a tweet
    @Transactional
    public void comment(Long id, String commentText) {
        UserEntity user = authService.getUserFromJwt();

        Optional<TweetEntity> optional = tweetRepository.findById(id);
        if (optional.isPresent()) {
            TweetEntity tweet = optional.get();
            CommentEntity comment = CommentEntity.builder()
                    .user(user)
                    .tweet(tweet)
                    .text(commentText)
                    .createdDate(Date.from(Instant.now()))
                    .build();
            tweet.getComments().add(comment);
            tweet.setCommentCounter(tweet.getCommentCounter() + 1);
            this.tweetRepository.save(tweet);
            commentRepository.save(comment);
        }else {
            throw new RuntimeException("Tweet not found");
        }
    }


    //Retweet a tweet
    @Transactional
    public void retweet(Long id) {
        UserEntity user = authService.getUserFromJwt();
        Optional<TweetEntity> optional = tweetRepository.findById(id);
        if (optional.isPresent()) {
            TweetEntity tweet = optional.get();
            RetweetEntity retweet = RetweetEntity.builder()
                    .user(user)
                    .tweet(tweet)
                    .likeCounter(0)
                    .retweetCounter(0)
                    .isLiked(false)
                    .isRetweeted(true)
                    .retweetCounter(tweet.getRetweetCounter())
                    .createdDate(Date.from(Instant.now()))
                    .build();
            tweet.setIsRetweeted(true);
            tweet.setRetweetCounter(tweet.getRetweetCounter() + 1);
            tweet.setRetweetedBy(retweet);
            retweetRepository.save(retweet);
        }else {
            throw new RuntimeException("Tweet not found");
        }
    }

    @Transactional (readOnly = true)
    public List<TweetResponseDto> isRetweeted() {
        return tweetRepository.findByIsRetweeted(true).stream()
                .map(this::mapTweetToDto)
                .collect(Collectors.toList());
    }


    // get all tweets for feed page and other pages within it
    @Transactional(readOnly = true)
    public Page<TweetResponseDto> getPaginatedFeed(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<TweetEntity> tweets = tweetRepository.findAll();
        List<RetweetEntity> retweets = retweetRepository.findAll();

        List<TweetResponseDto> combinedList = new ArrayList<>();
        combinedList.addAll(tweets.stream().map(this::mapTweetToDto).collect(Collectors.toList()));
        combinedList.addAll(retweets.stream().map(this::mapRetweetToDto).collect(Collectors.toList()));

        // Sort the combined list by created date in descending order
        combinedList.sort(Comparator.comparing(TweetResponseDto::getCreatedDate).reversed());

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), combinedList.size());
        List<TweetResponseDto> paginatedList = new ArrayList<>();
        if (startIndex < combinedList.size()) {
            paginatedList = combinedList.subList(startIndex, endIndex);
        }
        // Create a pageable response
        Page<TweetResponseDto> pageResponse = new PageImpl<>(paginatedList, pageable, combinedList.size());
        return pageResponse;
    }


    // get all comments
    @Transactional (readOnly = true)
    public List<CommentDto> getComments(Long id) {
        List<CommentDto> comments = this.commentRepository.findByTweet_Id(id).stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
        return comments;
    }



    /////////////////////// MAPPINGS ///////////////////////

    // for normal tweets
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

    // for retweets
    private TweetResponseDto mapRetweetToDto(RetweetEntity retweetEntity) {
        TweetEntity tweet = retweetEntity.getTweet();
        UserEntity publisher = tweet.getUser();
        RetweetDto retweetInfo = RetweetDto.builder()
                .id(retweetEntity.getId())
                .profilePic(retweetEntity.getUser().getProfilePic())
                .firstName(retweetEntity.getUser().getFirstName())
                .lastName(retweetEntity.getUser().getLastName())
                .username(retweetEntity.getUser().getUsername())
                .isLiked(false)
                .likeCounter(0)
                .isRetweeted(false)
                .retweetCounter(0)
                .createdDate(retweetEntity.getCreatedDate())
                .build();

        TweetResponseDto tweetResponseDto = TweetResponseDto.builder()
                .id(tweet.getId())
                .profilePic(publisher.getProfilePic())
                .firstName(publisher.getFirstName())
                .lastName(publisher.getLastName())
                .username(publisher.getUsername())
                .tweetText(tweet.getText())
                .commentCounter(0)
                .comments(new ArrayList<>())
                .retweetCounter(0)
                .likeCounter(0)
                .retweetedBy(retweetInfo)
                .createdDate(retweetEntity.getCreatedDate())
                .build();
        return tweetResponseDto;
    }


}
