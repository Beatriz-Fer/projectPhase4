package com.beatriz.twittercloneproject.controller;

import com.beatriz.twittercloneproject.dto.TweetResponseDto;
import com.beatriz.twittercloneproject.dto.UserDto;
import com.beatriz.twittercloneproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // get user by id
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    // update user with new details
    @PutMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long id, @RequestBody UserDto userDto){
        return ResponseEntity.ok(this.userService.updateUser(id, userDto));
    }

    // get user tweets
    @GetMapping("/user/userTweets/{id}")
    public ResponseEntity<List<TweetResponseDto>> getUserTweets(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(this.userService.getUserTweets(id));
    }
    // get user retweets
    @GetMapping("/user/userRetweets/{id}")
    public ResponseEntity<List<TweetResponseDto>> getUserRetweets(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(this.userService.getUserRetweets(id));
    }
    // get user liked tweets
    @GetMapping("/user/userLikedTweets/{id}")
    public ResponseEntity<List<TweetResponseDto>> getUserLikedTweets(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(this.userService.getUserLikedTweets(id));
    }

    //get all users
    @GetMapping("/user/userList")
    public ResponseEntity<List<UserDto>> getUsers(){
        return status(HttpStatus.OK).body(userService.getUsers());
    }

}
