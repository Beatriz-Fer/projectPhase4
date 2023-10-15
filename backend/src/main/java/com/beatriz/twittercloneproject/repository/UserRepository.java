package com.beatriz.twittercloneproject.repository;

import com.beatriz.twittercloneproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // find user by username
    Optional<UserEntity> findByUsername(String username);

    // checks to see if exists by email
    boolean existsByEmail(String email);
}
