package com.bit.datainkback.entity;

import com.bit.datainkback.dto.NotificationCacheDto;
import com.bit.datainkback.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "NotificationCache", timeToLive = 3600) // TTL 설정 (예: 1시간)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCache implements Serializable {

    @Id
    private Long id;
    private Long userId;
    private String content;
    private NotificationType type;
    private LocalDateTime timestamp;

    // 엔티티를 DTO로 변환하는 메서드
    public static NotificationCacheDto toDto(NotificationCache notificationCache) {
        return NotificationCacheDto.builder()
                .id(notificationCache.getId())
                .userId(notificationCache.getUserId())
                .content(notificationCache.getContent())
                .type(notificationCache.getType())
                .timestamp(notificationCache.getTimestamp())
                .build();
    }
}
