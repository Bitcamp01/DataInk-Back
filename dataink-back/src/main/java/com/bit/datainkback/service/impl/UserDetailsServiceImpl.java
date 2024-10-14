package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user not exist")
        );

        return CustomUserDetails.builder()
                .user(user)
                .build();
    }
}
