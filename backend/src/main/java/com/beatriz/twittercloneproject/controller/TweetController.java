package com.beatriz.twittercloneproject.controller;

import com.beatriz.twittercloneproject.dto.CommentDto;
import com.beatriz.twittercloneproject.dto.TweetDto;
import com.beatriz.twittercloneproject.dto.TweetResponseDto;
import com.beatriz.twittercloneproject.repository.TweetRepository;
import com.beatriz.twittercloneproject.service.AuthService;
import com.beatriz.twittercloneproject.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    //Create a Tweet
    @PostMapping("/tweet")
    public ResponseEntity<HttpStatus> createTweet(@RequestBody TweetDto tweetDto){
        tweetService.tweet(tweetDto);
        return ResponseEntity.ok().build();
    }


    // Like a tweet
    @PostMapping("/tweet/{id}/like")
    public ResponseEntity<HttpStatus> like(@PathVariable(name = "id") Long id){
        tweetService.like(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/tweet/is-liked")
    public ResponseEntity<List<TweetResponseDto>> isLiked(){
        return status(HttpStatus.OK).body(tweetService.isLiked());
    }


    // Comment on a tweet
    @PostMapping("/tweet/{id}/comment")
    public ResponseEntity<HttpStatus> comment(@PathVariable(name = "id") Long id, @RequestBody String commentText){
        tweetService.comment(id, commentText);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/tweet/{id}/comment-list")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable(name = "id") Long id){
        return status(HttpStatus.OK).body(tweetService.getComments(id));
    }


    //Share a tweet
    @PostMapping("/tweet/{id}/share")
    public ResponseEntity<HttpStatus> postRetweet(@PathVariable(name = "id") Long id){
        this.tweetService.retweet(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/tweet/is-retweeted")
    public ResponseEntity<List<TweetResponseDto>> isRetweeted(){
        return status(HttpStatus.OK).body(tweetService.isRetweeted());
    }


    //Get all tweets
    @GetMapping("/feed")
    public ResponseEntity<List<TweetResponseDto>> getFeed(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        Page<TweetResponseDto> paginatedTweets = tweetService.getPaginatedFeed(page, pageSize);
        return ResponseEntity.ok(paginatedTweets.getContent());
    }

}
