package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.NotificationRepository;
import com.bit.datainkback.repository.UserRepository;
import com.bit.datainkback.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

//    @Override
//    // 특정 사용자의 알림 목록 조회 (미읽음 알림만 포함 가능)
//    public List<Notification> getNotificationsByUserId(Long userId, boolean unreadOnly) {
//        if (unreadOnly) {
//            return notificationRepository.findByUser_UserIdAndIsReadFalse(userId);
//        } else {
//            return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
//        }
//    }

    @Override
    public List<NotificationDto> getNotificationDtosByUserId(Long userId, boolean unreadOnly) {
        List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notification -> NotificationDto.builder()
                        .notificationId(notification.getNotificationId())
                        .userId(notification.getUser().getUserId())
                        .userName(notification.getUser().getName())
                        .notificationType(notification.getNotificationType())
                        .content(notification.getContent())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .relatedId(notification.getRelatedId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDto> findAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId) {
        List<Long> notificationIds = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(loggedInUserId).stream()
                .map(notification -> notification.getNotificationId())
                .collect(Collectors.toList());

        return notificationRepository
                .searchAll(searchCondition, searchKeyword, pageable, startDate, endDate, notificationIds)
                .map(Notification::toDto);
    }

    @Override
    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
