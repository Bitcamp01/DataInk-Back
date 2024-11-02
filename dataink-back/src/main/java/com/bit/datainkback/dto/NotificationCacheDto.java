package com.bit.datainkback.dto;

import com.bit.datainkback.entity.NotificationCache;
import com.bit.datainkback.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationCacheDto {
    private Long id;
    private Long userId;
    private String content;
    private NotificationType type;
    private LocalDateTime timestamp;
    private String profile_image_url;

    // DTO를 엔티티로 변환하는 메서드 (필요 시 추가)
    public NotificationCache toEntity() {
        return NotificationCache.builder()
                .id(this.id)
                .userId(this.userId)
                .content(this.content)
                .type(this.type)
                .timestamp(this.timestamp)
                .build();
    }

    // from 메서드 추가
    public static NotificationCacheDto from(NotificationCache notificationCache, String profileImageUrl) {
        return NotificationCacheDto.builder()
                .id(notificationCache.getId())
                .userId(notificationCache.getUserId())
                .content(notificationCache.getContent())
                .type(notificationCache.getType())
                .timestamp(notificationCache.getTimestamp())
                .profile_image_url(profileImageUrl)
                .build();
    }
}
