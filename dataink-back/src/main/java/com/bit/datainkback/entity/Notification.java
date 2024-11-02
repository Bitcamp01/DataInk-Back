package com.bit.datainkback.entity;

import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@SequenceGenerator(
        name = "notificationSeqGenerator",
        sequenceName = "NOTIFICATION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notificationSeqGenerator"
    )
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림 수신자

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType; // 알림 유형

    @Column(name = "content", length = 500)
    private String content; // 알림 내용

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // 읽음 여부

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column(name = "related_id")
    private Long relatedId; // 관련된 데이터의 ID (예: 프로젝트 ID, 공지사항 ID 등)

    @Transient
    private LocalDateTime startDate;
    @Transient
    private LocalDateTime endDate;
    @Transient
    private String searchKeyword;
    @Transient
    private String searchCondition;


    public NotificationDto toDto() {
        return NotificationDto.builder()
                .notificationId(this.notificationId)
                .userId(this.user.getUserId())
                .userName(this.user.getName())
                .notificationType(this.notificationType)
                .content(this.content)
                .isRead(this.isRead)
                .createdAt(this.createdAt)
                .relatedId(this.relatedId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .searchKeyword(this.searchKeyword)
                .searchCondition(this.searchCondition)
                .build();
    }
}

