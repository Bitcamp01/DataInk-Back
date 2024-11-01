package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.NotificationCacheDto;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.NotificationCache;
import com.bit.datainkback.enums.NotificationType;
import com.bit.datainkback.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationCacheServiceImpl {
    private final NotificationRepository notificationRepository; // DB 레포지토리
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 사용
    private final ObjectMapper objectMapper; // ObjectMapper를 통한 변환

    // 최신 알림 3건 조회 (Redis에서 먼저 조회)
    public List<NotificationCacheDto> getLatestUnreadNotifications(Long userId) {
        String redisKey = "notifications:" + userId;
        List<Object> cacheNotifications = redisTemplate.opsForList().range(redisKey, 0, 2);

        List<NotificationCache> notifications = cacheNotifications.stream()
                .map(obj -> objectMapper.convertValue(obj, NotificationCache.class))
                .collect(Collectors.toList());

        // Redis에 데이터가 부족할 경우 DB에서 보충
        if (notifications.size() < 3) {
            List<Notification> dbNotifications = notificationRepository.findTop3ByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
            notifications.addAll(dbNotifications.stream().map(Notification::toCache).collect(Collectors.toList()));
        }

        return notifications.stream().map(NotificationCache::toDto).collect(Collectors.toList());
    }

    // 알림 읽음 처리
    public void markNotificationAsRead(Long notificationId) {
        // DB에서 읽음 상태 업데이트
        notificationRepository.updateIsRead(notificationId, true);

        // Redis에서도 해당 알림 삭제
        redisTemplate.delete("notifications:" + notificationId);
    }

    // Redis에 알림 추가 및 확인
    public void addNotificationToRedis(Notification notification) {
        NotificationCache notificationCache = NotificationCache.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE) // 고유 ID 생성
                .timestamp(LocalDateTime.now())
                .content("(공지) " + notification.getContent())
                .type(NotificationType.NOTICE)
                .userId(notification.getUser().getUserId())
                .build();

        String redisKey = "notifications:" + notification.getUser().getUserId();
        try {
            redisTemplate.opsForList().rightPush(redisKey, notificationCache);
            System.out.println("알림이 Redis에 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            System.err.println("Redis에 알림 저장 실패: " + e.getMessage());
        }

        // Redis에 저장된 알림 확인
        List<Object> savedNotifications = redisTemplate.opsForList().range(redisKey, 0, -1);
        savedNotifications.forEach(obj -> {
            NotificationCache savedNotification = objectMapper.convertValue(obj, NotificationCache.class);
            System.out.println("Redis에 저장된 알림 내용: " + savedNotification.getContent());
        });
    }
}
