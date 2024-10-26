package com.bit.datainkback.controller;

import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profileimg")
@RequiredArgsConstructor
@Slf4j
public class ProfileImgController {
    private final FileUtils fileUtils;
    private final MypageService mypageService;

    @GetMapping("/profile-image")
    public ResponseEntity<String> getProfileImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        String profileImageUrl = mypageService.getUserDetail(loggedInUserId).getProfileImageUrl();
        if (profileImageUrl != null) {
            return ResponseEntity.ok(profileImageUrl);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로필 이미지가 없습니다.");
        }
    }

    // 프로필 및 배경 이미지 업로드 또는 업데이트 API 통합
    @PostMapping("/update-profile-image")
    public ResponseEntity<ResponseDto<UserDetailDto>> updateProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDto<UserDetailDto> responseDto = new ResponseDto<>();
        try {
            // 사용자 ID 확인
            Long loggedInUserId = customUserDetails.getUser().getUserId();
            String directory = "profile-img/";

            /// 파일 업로드 및 유저 디테일 정보 업데이트
            UserDetailDto userDetailDto = fileUtils.updateImg(file, directory, true);
            String profileImgType = file.getContentType();

            // 서비스 레이어를 통해 DB에 저장
            UserDetailDto updatedUserDetail = mypageService.updateUserProfileImage(
                    loggedInUserId,
                    profileImgType,
                    userDetailDto.getProfileImgName(),
                    userDetailDto.getProfileImageUrl(),
                    file
            );
            // 응답 DTO 구성
            responseDto.setItem(updatedUserDetail); // 단일 객체 설정
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Profile image updated successfully");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error updating profile image: {}", e.getMessage());

            // 오류 응답 DTO 구성
            ResponseDto<UserDetailDto> errorResponseDto = new ResponseDto<>();
            errorResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponseDto.setStatusMessage("Profile image update failed");

            return ResponseEntity.internalServerError().body(errorResponseDto);
        }
    }

    // 프로필 이미지 삭제 API
    @DeleteMapping("/delete-profile-image")
    public ResponseEntity<ResponseDto<UserDetailDto>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("file") MultipartFile file) {

        ResponseDto<UserDetailDto> responseDto = new ResponseDto<>();
        try {
            Long loggedInUserId = customUserDetails.getUser().getUserId();
            String directory = "background-img/";

            UserDetailDto userDetailDto = mypageService.getUserDetail(loggedInUserId);
            String profileImgType = file.getContentType();

                UserDetailDto updatedUserDetail = mypageService.updateUserProfileImage(
                        loggedInUserId,
                        null,
                        null,
                        profileImgType,
                        file
                );

                responseDto.setItem(updatedUserDetail);
                responseDto.setStatusCode(HttpStatus.OK.value());
                responseDto.setStatusMessage("Profile image deleted successfully");

                return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error deleting profile image: {}", e.getMessage());

            ResponseDto<UserDetailDto> errorResponseDto = new ResponseDto<>();
            errorResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponseDto.setStatusMessage("Profile image deletion failed");

            return ResponseEntity.internalServerError().body(errorResponseDto);
        }
    }
}
