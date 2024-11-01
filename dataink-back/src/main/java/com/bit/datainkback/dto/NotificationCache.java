package com.bit.datainkback.dto;

import com.bit.datainkback.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "NotificationCache", timeToLive = 3600) // TTL 설정 (예: 1시간)
@Getter
@Setter
public class NotificationCache implements Serializable {

    @Id
    private String id;
    private String userId;
    private String content;
    private NotificationType type;
    private LocalDateTime timestamp;
}
