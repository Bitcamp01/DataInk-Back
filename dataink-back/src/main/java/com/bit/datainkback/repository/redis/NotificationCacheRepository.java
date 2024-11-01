package com.bit.datainkback.repository.redis;

import com.bit.datainkback.entity.NotificationCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationCacheRepository extends CrudRepository<NotificationCache, Long> {
    // 특정 사용자 ID에 대해 최신 알림 3건을 조회합니다.
    List<NotificationCache> findTop3ByUserIdOrderByTimestampDesc(Long userId);

    // 사용자 ID로 알림을 조회합니다.
    List<NotificationCache> findByUserId(Long userId);

    // 알림 ID로 알림을 삭제합니다.
    void deleteById(Long id);
}
