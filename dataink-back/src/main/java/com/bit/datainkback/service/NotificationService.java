package com.bit.datainkback.service;

import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.entity.Notification;

import java.util.List;

public interface NotificationService {
    public void markAsRead(Long notificationId);

    List<NotificationDto> getNotificationDtosByUserId(Long userId, boolean unreadOnly);
}
