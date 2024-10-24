package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.repository.UserDetailRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkPassword(String loggedInUserId, String inputPassword) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(inputPassword, user.getPassword());
    }

    @Override
    public UserDetailDto updateUserProfile(Long loggedInUserId, UserDetailDto userDetailDto) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetail userDetail = userDetailRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        userDetail.setNickname(userDetailDto.getNickname());
        userDetail.setAddr(userDetailDto.getAddr());
        userDetail.setDep(userDetailDto.getDep());

        UserDetail updatedUserDetail = userDetailRepository.save(userDetail);

        return updatedUserDetail.toDto();
    }

    @Override
    public UserDetailDto getUserDetail(Long userId) {
        UserDetail userDetail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userDetail.toDto();
    }

    @Override
    public UserDetailDto updateProfileImage(Long userId, String profileImageUrl) {
        UserDetail userDetail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userDetail.setProfileImageUrl(profileImageUrl);
        UserDetail updatedUserDetail = userDetailRepository.save(userDetail);
        return updatedUserDetail.toDto(); // 업데이트된 정보 반환
    }

    @Override
    public UserDetailDto updateBackgroundImage(Long userId, String backgroundImageUrl) {
        UserDetail userDetail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userDetail.setBackgroundImageUrl(backgroundImageUrl);
        UserDetail updatedUserDetail = userDetailRepository.save(userDetail);
        return updatedUserDetail.toDto(); // 업데이트된 정보 반환
    }
}
