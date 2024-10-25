package com.bit.datainkback.controller;

import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDetailDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/backgroundimg")
@RequiredArgsConstructor
@Slf4j
public class BackgroundController {
    private final FileUtils fileUtils;
    private final MypageService mypageService;

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

    // 배경 이미지 업로드 또는 업데이트 API
    @PostMapping("/update-background-image")
    public ResponseEntity<ResponseDto<UserDetailDto>> updateBackgroundImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDto<UserDetailDto> responseDto = new ResponseDto<>();
        try {
            Long loggedInUserId = customUserDetails.getUser().getUserId();
            String directory = "background-img/";

            UserDetailDto userDetailDto = fileUtils.updateImg(file, directory, false);
            String backgroundImgType = file.getContentType();

            UserDetailDto updatedUserDetail = mypageService.updateUserBackgroundImage(
                    loggedInUserId,
                    backgroundImgType,
                    userDetailDto.getBackgroundImgName(),
                    userDetailDto.getBackgroundImageUrl(),
                    file
            );
            responseDto.setItem(updatedUserDetail); // 단일 객체 설정
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Background image updated successfully");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error updating background image: {}", e.getMessage());

            ResponseDto<UserDetailDto> errorResponseDto = new ResponseDto<>();
            errorResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponseDto.setStatusMessage("Background image update failed");

            return ResponseEntity.internalServerError().body(errorResponseDto);
        }
    }

    // 배경 이미지 삭제 API
    @DeleteMapping("/delete-background-image")
    public ResponseEntity<ResponseDto<UserDetailDto>> deleteBackgroundImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("file") MultipartFile file) {

        ResponseDto<UserDetailDto> responseDto = new ResponseDto<>();
        try {
            Long loggedInUserId = customUserDetails.getUser().getUserId();

            UserDetailDto userDetailDto = mypageService.getUserDetail(loggedInUserId);
            String backgroundImgType = file.getContentType();

                UserDetailDto updatedUserDetail = mypageService.updateUserBackgroundImage(
                        loggedInUserId,
                        backgroundImgType,
                        null,
                        null,
                        file
                );

                responseDto.setItem(updatedUserDetail);
                responseDto.setStatusCode(HttpStatus.OK.value());
                responseDto.setStatusMessage("Background image deleted successfully");

                return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error deleting background image: {}", e.getMessage());
            ResponseDto<UserDetailDto> errorResponseDto = new ResponseDto<>();
            errorResponseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponseDto.setStatusMessage("Background image deletion failed");

            return ResponseEntity.internalServerError().body(errorResponseDto);
        }
    }
}

