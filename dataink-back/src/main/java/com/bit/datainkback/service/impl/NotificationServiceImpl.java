package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.repository.NotificationRepository;
import com.bit.datainkback.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    // 특정 사용자의 알림 목록 조회 (미읽음 알림만 포함 가능)
    public List<Notification> getNotificationsByUserId(Long userId, boolean unreadOnly) {
        if (unreadOnly) {
            return notificationRepository.findByUser_UserIdAndIsReadFalse(userId);
        } else {
            return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        }
    }

    @Override
    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}