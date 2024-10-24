package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.repository.UserDetailRepository;
import com.bit.datainkback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user not exist")
        );

        return CustomUserDetails.builder()
                .user(user)
                .build();
    }

    public UserDetail getUserDetailByUserId(Long userId) {
        return userDetailRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("UserDetail not found for userId: " + userId)
        );
    }
}
