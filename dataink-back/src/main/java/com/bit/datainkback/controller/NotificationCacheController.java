package com.bit.datainkback.controller;

import com.bit.datainkback.dto.NotificationCacheDto;
import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.NotificationCache;
import com.bit.datainkback.service.impl.NotificationCacheServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification-cache")
@RequiredArgsConstructor
public class NotificationCacheController {
    private final NotificationCacheServiceImpl notificationCacheService;

    // 최신 알림 3건 조회
    @GetMapping("/latest")
    public ResponseEntity<List<NotificationCacheDto>> getLatestNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        List<NotificationCacheDto> notifications = notificationCacheService.getLatestUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationCacheService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }
}
