package com.beatriz.twittercloneproject.service;

import com.beatriz.twittercloneproject.dto.*;
import com.beatriz.twittercloneproject.entity.UserEntity;
import com.beatriz.twittercloneproject.repository.UserRepository;
import com.beatriz.twittercloneproject.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthService {

      private final UserRepository userRepository;
      private final PasswordEncoder passwordEncoder;
      private final AuthenticationManager authenticationManager;
      private final JwtProvider jwtProvider;


      // Register a new user
   @Transactional
    public void register(RegisterRequestDto registerRequestDto){
        UserEntity user = new UserEntity();
        user.setFirstName(registerRequestDto.getFirstName());
        user.setLastName(registerRequestDto.getLastName());
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setProfilePic("/assets/images/profile-image-1.jpg");
        user.setBio("This is a dummy bio.");
        userRepository.save(user);
    }

    // Login
    public AuthResponseDto login(LoginRequestDto loginRequestDto){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authenticationToken = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken().getRefreshToken();
        String username = loginRequestDto.getUsername();

        return AuthResponseDto.builder()
                .id(userRepository.findByUsername(username).get().getId())
                .authenticationToken(authenticationToken)
                .refreshToken(refreshToken)
                .username(username)
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .build();
    }

    // Get user from JWT
    public UserEntity getUserFromJwt(){
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getSubject()).orElseThrow();
    }

    // Logout
    public void logout(RefreshTokenDto refreshTokenDto){
        jwtProvider.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        SecurityContextHolder.clearContext();
    }

    // Refresh token
    public LoginResponseDto refreshToken(RefreshTokenDto refreshTokenDto){
        this.jwtProvider.validateRefreshToken(refreshTokenDto.getRefreshToken());
        String authenticationToken = this.jwtProvider.generateTokenWithUserName(refreshTokenDto.getUsername());
        return LoginResponseDto.builder()
                .authenticationToken(authenticationToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .username(refreshTokenDto.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .build();
    }

}
