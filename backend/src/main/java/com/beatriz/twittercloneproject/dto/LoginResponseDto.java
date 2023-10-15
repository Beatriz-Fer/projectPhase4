package com.beatriz.twittercloneproject.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String authenticationToken;
    private String refreshToken;
    private String id;
    private String username;
    private Instant expiresAt;
}
