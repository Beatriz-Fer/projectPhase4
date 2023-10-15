package com.beatriz.twittercloneproject.security;

import com.beatriz.twittercloneproject.entity.RefreshTokenEntity;
import com.beatriz.twittercloneproject.repository.RefreshTokenRepository;
import org.springframework.security.core.userdetails.User;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    // JWT token provider

    private final JwtEncoder jwtEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return generateTokenWithUserName(principal.getUsername());
    }

    public String generateTokenWithUserName(String username) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
                .subject(username)
                .claim("scope", "ROLE_USER")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Long getJwtExpirationInMillis(){return this.jwtExpirationInMillis;}

    @Transactional
    public RefreshTokenEntity generateRefreshToken(){
        return this.refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .build());
    }
    @Transactional(readOnly = true)
    public void validateRefreshToken(String refreshToken){
        this.refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow();
    }
    @Transactional
    public void deleteRefreshToken(String refreshToken){
        this.refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
