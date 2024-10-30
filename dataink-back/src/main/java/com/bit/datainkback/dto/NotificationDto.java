package com.bit.datainkback.dto;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long notificationId;
    private Long userId; // 알림 수신자 ID
    private NotificationType notificationType; // 알림 유형
    private String content; // 알림 내용
    private boolean isRead; // 읽음 여부
    private LocalDateTime createdAt; // 생성일시
    private Long relatedId; // 관련 데이터 ID (예: 프로젝트 ID, 공지사항 ID 등)

    public Notification toEntity(User user) {
        return Notification.builder()
                .notificationId(this.notificationId)
                .user(user)
                .notificationType(this.notificationType)
                .content(this.content)
                .isRead(this.isRead)
                .createdAt(this.createdAt)
                .relatedId(this.relatedId)
                .build();
    }
}