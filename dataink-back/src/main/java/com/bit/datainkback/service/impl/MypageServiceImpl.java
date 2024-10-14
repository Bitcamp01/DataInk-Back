package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkPassword(String loggedInUserId, String inputPassword) {

        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 입력된 비밀번호와 저장된 비밀번호 비교
        return passwordEncoder.matches(inputPassword, user.getPassword());
    }
}
