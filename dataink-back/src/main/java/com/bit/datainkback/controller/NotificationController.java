package com.bit.datainkback.controller;

import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

//    @GetMapping("/notification-info")
//    public ResponseEntity<List<Notification>> getNotificationsByUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails, Boolean unreadOnly) {
//        ResponseDto<List<Notification>> responseDto = new ResponseDto<>();
//
//        try {
//            Long userId = customUserDetails.getUser().getUserId();
//            List<Notification> notifications = notificationService.getNotificationsByUserId(userId, unreadOnly);
//
//            responseDto.setStatusCode(HttpStatus.OK.value());
//            responseDto.setStatusMessage("Get notification-Info successfully");
//            responseDto.setItem(notifications);
//
//            return ResponseEntity.ok(responseDto.getItem());
//        } catch (Exception e) {
//            log.error("Get notification-Info error: {}", e.getMessage());
//            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDto.setStatusMessage(e.getMessage());
//            return ResponseEntity.internalServerError().body(responseDto.getItem());
//        }
//
//    }
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotificationsByUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails, Boolean unreadOnly) {
        ResponseDto<List<NotificationDto>> responseDto = new ResponseDto<>();

        try {
            Long userId = customUserDetails.getUser().getUserId();
            List<NotificationDto> notificationDtos = notificationService.getNotificationDtosByUserId(userId, unreadOnly != null && unreadOnly);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("Get notification-Info successfully");
            responseDto.setItem(notificationDtos);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Get notification-Info error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
}
