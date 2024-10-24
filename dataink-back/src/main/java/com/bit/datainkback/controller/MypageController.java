package com.bit.datainkback.controller;

import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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



}
