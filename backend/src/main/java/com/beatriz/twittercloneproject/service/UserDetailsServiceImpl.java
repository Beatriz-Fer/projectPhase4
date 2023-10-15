package com.beatriz.twittercloneproject.service;

import com.beatriz.twittercloneproject.entity.UserEntity;
import com.beatriz.twittercloneproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    // load user by username - part of authenication process
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
        return new org.springframework.security.core.userdetails.User(userEntity.getUsername(), userEntity.getPassword(),
                true, true,
                true, true, this.getAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role){
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

}
