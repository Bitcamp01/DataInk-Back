package com.bit.datainkback.controller;

import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.dto.ProjectDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

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

    @GetMapping("/search-alarm")
    public ResponseEntity<?> getAlarmBySearch(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @RequestParam(value = "searchCondition", required = false, defaultValue = "all") String searchCondition,
                                                @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                                @RequestParam(value = "startDate", required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(value = "endDate", required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                @PageableDefault(page = 0, size = 10) Pageable pageable) {
        ResponseDto<NotificationDto> responseDto = new ResponseDto<>();
        Long loggedInUserId = customUserDetails.getUser().getUserId();

        try {
            Page<NotificationDto> notificationDtoList = notificationService.findAll(searchCondition, searchKeyword, pageable, startDate, endDate, loggedInUserId);
            log.info("notificationDtoList: {}", notificationDtoList);

            responseDto.setPageItems(notificationDtoList);
            responseDto.setItem(NotificationDto.builder()
                    .searchCondition(searchCondition)
                    .searchKeyword(searchKeyword)
                    .build());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            log.error("getAlarmBySearch error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

}
