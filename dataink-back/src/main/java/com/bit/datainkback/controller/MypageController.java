package com.bit.datainkback.controller;

import com.bit.datainkback.dto.*;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import com.bit.datainkback.entity.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {
    private final MypageService mypageService;

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
    public ResponseEntity<ResponseDto<UserDetailDto>> updateProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserDetailDto userDetailDto) {
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

    @GetMapping("/details")
    public ResponseEntity<UserDetailDto> getUserDetail(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        UserDetailDto userDetailDto = mypageService.getUserDetail(loggedInUserId);
        return ResponseEntity.ok(userDetailDto);
    }

    @GetMapping("/profile-intro")
    public ResponseEntity<String> getProfileIntro(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        String profileIntro = mypageService.getUserProfileIntro(loggedInUserId);
        return ResponseEntity.ok(profileIntro);
    }

    @PostMapping("/profile-intro")
    public ResponseEntity<UserDetailDto> updateProfileIntro(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody String profileIntro) {
        Long loggedInUserId = customUserDetails.getUser().getUserId();
        UserDetailDto updatedDetail = mypageService.updateUserProfileIntro(loggedInUserId, profileIntro);
        return ResponseEntity.ok(updatedDetail);
    }
}