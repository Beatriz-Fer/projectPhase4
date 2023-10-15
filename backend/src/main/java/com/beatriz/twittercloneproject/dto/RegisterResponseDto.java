package com.beatriz.twittercloneproject.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDto {
    boolean isEmailUnique;
}
