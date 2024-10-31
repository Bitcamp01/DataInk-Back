package com.bit.datainkback.service;

import com.bit.datainkback.entity.Notification;

import java.util.List;

public interface NotificationService {
    public List<Notification> getNotificationsByUserId(Long userId, boolean unreadOnly);

    public void markAsRead(Long notificationId);
}
