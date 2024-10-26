package com.bit.datainkback.service;

import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface MypageService {
    boolean checkPassword(String loggedInUserId, String inputPassword);

    UserDetailDto updateUserProfile(Long loggedInUserId, UserDetailDto userDetailDto);

    UserDetailDto getUserDetail(Long id);

    UserDetailDto updateUserProfileImage(Long loggedInUserId,String backgroundImgType, String profileImgName, String profileImageUrl, MultipartFile file);

    UserDetailDto updateUserBackgroundImage(Long loggedInUserId,String backgroundImgType, String backgroundImgName, String backgroundImageUrl, MultipartFile file);

    UserDetailDto updateUserProfileIntro(Long loggedInUserId, String profileIntro);

    String getUserProfileIntro(Long loggedInUserId);
}
