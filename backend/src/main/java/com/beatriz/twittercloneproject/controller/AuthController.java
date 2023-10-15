package com.beatriz.twittercloneproject.controller;

import com.beatriz.twittercloneproject.dto.*;
import com.beatriz.twittercloneproject.repository.UserRepository;
import com.beatriz.twittercloneproject.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/user")
public class AuthController {

    private final UserRepository userRepository;

    private final AuthService authService;

    // to register a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return new ResponseEntity<>("User registered successfully", OK);
    }

    // to login
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto loginRequestDto){
        return authService.login(loginRequestDto);
    }

    // to logout
    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(@RequestBody RefreshTokenDto refreshTokenDto){
        this.authService.logout(refreshTokenDto);
        return ResponseEntity.ok().build();
    }

    // to refresh the token
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto ){
        return ResponseEntity.ok().body(this.authService.refreshToken(refreshTokenDto));
    }

    // to check if the email is unique
    @PostMapping("/check-email-unique")
    public RegisterResponseDto checkEmailUniqueness(@RequestBody RegisterRequestDto request) {
        RegisterResponseDto response = new RegisterResponseDto();
        if (userRepository.count() == 0) {
            // If there are no emails in the repository, consider the email as unique
            response.setEmailUnique(true);
        } else {
            // Check if the email already exists in the database
            boolean isEmailUnique = !userRepository.existsByEmail(request.getEmail());
            response.setEmailUnique(isEmailUnique);
        }
        return response;
    }

}
