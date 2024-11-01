package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 사용자의 읽지 않은 알림 조회
    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);

    // 특정 사용자의 알림을 최신순으로 조회
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findTop3ByUser_UserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.relatedId = :notificationId")
    void updateIsRead(@Param("notificationId") Long notificationId, @Param("isRead") boolean isRead);
}

