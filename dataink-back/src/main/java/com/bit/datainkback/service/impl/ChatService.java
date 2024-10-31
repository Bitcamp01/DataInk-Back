package com.bit.datainkback.service.impl;

import com.bit.datainkback.entity.ChatMessage;
import com.bit.datainkback.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final ListOperations<String, ChatMessage> listOps;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(RedisTemplate<String, ChatMessage> redisTemplate, ChatMessageRepository chatMessageRepository) {
        this.redisTemplate = redisTemplate;
        this.listOps = redisTemplate.opsForList();
        this.chatMessageRepository = chatMessageRepository;
    }

    public void saveMessageToRedis(ChatMessage message) {
        String key = "chatroom:" + message.getRoomId();
        listOps.rightPush(key, message);
        redisTemplate.expire(key, Duration.ofDays(1));  // 1일 후 만료
    }

    public void backupMessagesToRdbms(String roomId) {
        List<ChatMessage> messages = listOps.range("chatroom:" + roomId, 0, -1);
        messages.forEach(chatMessageRepository::save);  // RDBMS로 저장
        redisTemplate.delete("chatroom:" + roomId);  // Redis에서 삭제
    }

    public Set<String> getAllChatRoomIds() {
        Set<String> roomIds = new HashSet<>();
        chatMessageRepository.findAll().stream().map(x->roomIds.add(x.getRoomId().toString()));
        return roomIds;
    }
}