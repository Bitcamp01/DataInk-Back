package com.bit.datainkback.scheduler;

import com.bit.datainkback.service.impl.ChatService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class ChatBackupScheduler {

    private final ChatService chatService;

    public ChatBackupScheduler(ChatService chatService) {
        this.chatService = chatService;
    }

    // 매일 자정에 Redis의 데이터를 RDBMS로 백업
    @Scheduled(cron = "0 0 0 * * ?")
    public void backupChatRooms() {
        Set<String> chatRoomIds = chatService.getAllChatRoomIds();

        for (String roomKey : chatRoomIds) {
            String roomId = roomKey.replace("chatroom:", ""); // 키에서 접두사 제거
            chatService.backupMessagesToRdbms(roomId);
        }
    }
}