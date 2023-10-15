package com.beatriz.twittercloneproject.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String profilePic;
    private String bio;
    boolean isEmailUnique;
}
