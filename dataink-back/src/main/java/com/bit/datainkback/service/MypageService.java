package com.bit.datainkback.service;

import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.dto.UserDto;

public interface MypageService {
    boolean checkPassword(String loggedInUserId, String inputPassword);

    UserDetailDto updateUserProfile(Long loggedInUserId, UserDetailDto userDetailDto);

    UserDetailDto getUserDetail(Long id);

    UserDetailDto updateProfileImage(Long userId, String profileImageUrl);

    UserDetailDto updateBackgroundImage(Long id, String fileUrl);
}
