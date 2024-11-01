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
}
