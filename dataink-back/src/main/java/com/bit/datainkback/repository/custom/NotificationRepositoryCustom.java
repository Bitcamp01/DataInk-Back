package com.bit.datainkback.repository.custom;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepositoryCustom {
    Page<Notification> searchAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, List<Long> notificationIds);
}
