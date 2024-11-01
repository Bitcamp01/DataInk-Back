package com.bit.datainkback.config;

import com.bit.datainkback.entity.NotificationCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, NotificationCache> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, NotificationCache> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<NotificationCache> serializer = new Jackson2JsonRedisSerializer<>(NotificationCache.class);
        template.setDefaultSerializer(serializer);

        return template;
    }
}

