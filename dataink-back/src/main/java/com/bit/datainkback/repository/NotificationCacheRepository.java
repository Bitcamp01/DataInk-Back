package com.bit.datainkback.repository;

import com.bit.datainkback.dto.NotificationCache;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NotificationCacheRepository extends CrudRepository<NotificationCache, String> {
    List<NotificationCache> findByUserId(String userId);
}
