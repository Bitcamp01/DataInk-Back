package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.dto.UserDto;
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

        // 입력된 비밀번호와 저장된 비밀번호 비교
        return passwordEncoder.matches(inputPassword, user.getPassword());
    }

    @Override
    public UserDetailDto updateUserProfile(Long loggedInUserId, UserDetailDto userDetailDto) {
        // 사용자 조회
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자 세부 정보 조회
        UserDetail userDetail = userDetailRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User detail not found"));

        // 사용자 정보 업데이트
        userDetail.setNickname(userDetailDto.getNickname());
        userDetail.setAddr(userDetailDto.getAddr());
        userDetail.setDep(userDetailDto.getDep());

        // 변경된 정보 저장
        UserDetail updatedUserDetail = userDetailRepository.save(userDetail);

        // 업데이트된 사용자 정보를 UserDetailDto로 변환하여 반환 (toDto 사용)
        return updatedUserDetail.toDto();
    }

}
