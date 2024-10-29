package com.bit.datainkback.service.impl;

import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.repository.UserDetailRepository;
import com.bit.datainkback.repository.UserProjectRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

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
    public UserDetailDto getUserDetail(Long loggedInUserId) {
        UserDetail userDetail = userDetailRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userDetail.toDto();
    }

    @Override
    public UserDetailDto updateUserProfileImage(Long loggedInUserId, String profileImgType, String profileImgName, String profileImageUrl, MultipartFile multipartFile) {
        // 사용자 정보 조회
        UserDetail userDetail = userDetailRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 프로필 이미지 정보 업데이트
        userDetail.setProfileImgName(profileImgName);
        userDetail.setProfileImageUrl(profileImageUrl);
        userDetail.setProfileImgOriginname(multipartFile.getOriginalFilename());
        userDetail.setProfileImgType(profileImgType);

        // 저장된 엔티티를 DB에 반영
        userDetailRepository.save(userDetail);

        // 업데이트된 엔티티를 DTO로 변환 후 반환
        return userDetail.toDto();
    }

    @Override
    public UserDetailDto updateUserBackgroundImage(Long loggedInUserId,String backgroundImgType, String backgroundImgName, String backgroundImageUrl, MultipartFile multipartFile) {
        // 사용자 정보 조회
        UserDetail userDetail = userDetailRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 배경 이미지 정보 업데이트
        userDetail.setBackgroundImgName(backgroundImgName);
        userDetail.setBackgroundImageUrl(backgroundImageUrl);
        userDetail.setBackgroundImgOriginname(multipartFile.getOriginalFilename());
        userDetail.setBackgroundImgType(backgroundImgType);

        // 저장된 엔티티를 DB에 반영
        userDetailRepository.save(userDetail);

        // 업데이트된 엔티티를 DTO로 변환 후 반환
        return userDetail.toDto();
    }

    @Override
    public UserDetailDto updateUserProfileIntro(Long loggedInUserId, String profileIntro) {
        UserDetail userDetail = userDetailRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userDetail.setProfileIntro(profileIntro);
        userDetailRepository.save(userDetail);
        return userDetail.toDto();
    }

    @Override
    public String getUserProfileIntro(Long loggedInUserId) {
        UserDetail userDetail = userDetailRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userDetail.getProfileIntro();
    }
}