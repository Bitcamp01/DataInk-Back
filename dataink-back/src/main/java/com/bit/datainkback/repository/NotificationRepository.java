package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.custom.NotificationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    // 특정 사용자의 읽지 않은 알림 조회
    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);

    // 특정 사용자의 알림을 최신순으로 조회
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
