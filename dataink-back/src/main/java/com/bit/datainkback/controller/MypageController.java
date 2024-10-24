package com.bit.datainkback.controller;

import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.entity.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {
    private final MypageService mypageService;
    private final FileUtils fileUtils;

    @Value("${naver.cloud.bucket.name}")
    private String bucketName;


    @PostMapping("/password-check")
    public ResponseEntity<?> checkPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserDto userDto) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        String inputPassword = userDto.getPassword();

        try {
            String loggedInUserId = userDetails.getUsername();
            log.info("Password check for user: {}", loggedInUserId);

            boolean isPasswordCorrect = mypageService.checkPassword(loggedInUserId, inputPassword);

            log.info("Password check for password: {}", isPasswordCorrect);

            if (isPasswordCorrect) {
                responseDto.setStatusCode(HttpStatus.OK.value());
                responseDto.setStatusMessage("Password verified successfully");
                return ResponseEntity.ok(responseDto);
            } else {
                responseDto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                responseDto.setStatusMessage("Incorrect password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
            }
        } catch (Exception e) {
            log.error("Password check error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    // 프로필 업데이트 API 추가
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserDetailDto userDetailDto) {
        ResponseDto<UserDetailDto> responseDto = new ResponseDto<>();

        try {
            Long loggedInUserId = customUserDetails.getUser().getUserId();
            UserDetailDto updatedUserDetail = mypageService.updateUserProfile(loggedInUserId, userDetailDto);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Profile updated successfully");
            responseDto.setItem(updatedUserDetail);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Profile update error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping
    public ResponseEntity<UserDetailDto> getMypageInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        UserDetailDto userDetail = mypageService.getUserDetail(loggedInUserId);
        return ResponseEntity.ok(userDetail);
    }

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

    @GetMapping("/background-image")
    public ResponseEntity<String> getBackgroundImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        String backgroundImageUrl = mypageService.getUserDetail(loggedInUserId).getBackgroundImageUrl();
        if (backgroundImageUrl != null) {
            return ResponseEntity.ok(backgroundImageUrl);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("배경 이미지가 없습니다.");
        }
    }

    // 프로필 및 배경 이미지 업로드 또는 업데이트 API 통합
//    @PostMapping("/upload-image")
//    public ResponseEntity<String> uploadOrUpdateImage(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("type") String type,
//            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        try {
//            Long loggedInUserId = customUserDetails.getUser().getUserId();
//            String currentImageUrl;
//
//            if ("profile".equalsIgnoreCase(type)) {
//                currentImageUrl = mypageService.getUserDetail(loggedInUserId).getProfileImageUrl();
//            } else if ("background".equalsIgnoreCase(type)) {
//                currentImageUrl = mypageService.getUserDetail(loggedInUserId).getBackgroundImageUrl();
//            } else {
//                return ResponseEntity.badRequest().body("Invalid image type. Type should be 'profile' or 'background'.");
//            }
//
//            // 기존 이미지 삭제
//            if (currentImageUrl != null) {
//                String currentFileName = extractFileName(currentImageUrl);
//                fileUtils.deleteFile(type + "-img/", currentFileName);
//            }
//
//            // 새로운 이미지 업로드
//            String fileName = fileUtils.uploadFile(type + "-img/", file);
//            String fileUrl = fileUtils.getFileUrl(type + "-img/", fileName);
//
//            // 이미지 URL 업데이트
//            if ("profile".equalsIgnoreCase(type)) {
//                mypageService.updateProfileImage(loggedInUserId, fileUrl);
//            } else {
//                mypageService.updateBackgroundImage(loggedInUserId, fileUrl);
//            }
//
//            return ResponseEntity.ok(fileUrl);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드에 실패했습니다.");
//        }
//    }

    // 프로필 이미지 삭제 API
    @DeleteMapping("/delete-profile-image")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return deleteImage(customUserDetails, "profile");
    }

    // 배경 이미지 삭제 API
    @DeleteMapping("/delete-background-image")
    public ResponseEntity<String> deleteBackgroundImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return deleteImage(customUserDetails, "background");
    }

    private ResponseEntity<String> deleteImage(CustomUserDetails customUserDetails, String type) {
        try {
            Long loggedInUserId = customUserDetails.getUser().getUserId();
            String currentImageUrl;

            if ("profile".equals(type)) {
                currentImageUrl = mypageService.getUserDetail(loggedInUserId).getProfileImageUrl();
            } else {
                currentImageUrl = mypageService.getUserDetail(loggedInUserId).getBackgroundImageUrl();
            }

            if (currentImageUrl != null) {
                String currentFileName = extractFileName(currentImageUrl);
                fileUtils.deleteFile(type + "-img", currentFileName);
                if ("profile".equals(type)) {
                    mypageService.updateProfileImage(loggedInUserId, null);
                } else {
                    mypageService.updateBackgroundImage(loggedInUserId, null);
                }
            }
            return ResponseEntity.ok(type + " 이미지가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(type + " 이미지 삭제에 실패했습니다.");
        }
    }

    private String extractFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}