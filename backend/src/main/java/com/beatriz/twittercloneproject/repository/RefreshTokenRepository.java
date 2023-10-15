package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    // finds by refresh token
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    // deletes by refresh token
    void deleteByRefreshToken(String refreshToken);

}
