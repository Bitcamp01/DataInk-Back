package com.bit.datainkback.service;

import com.bit.datainkback.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
public interface NotificationService {
    public void markAsRead(Long notificationId);

    List<NotificationDto> getNotificationDtosByUserId(Long userId, boolean unreadOnly);

    Page<NotificationDto> findAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long loggedInUserId);
}
