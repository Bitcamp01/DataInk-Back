//package com.bit.datainkback.controller;
//import com.bit.datainkback.entity.ChatMessage;
//import com.bit.datainkback.entity.CustomUserDetails;
//import com.bit.datainkback.repository.ChatMessageRepository;
//import com.bit.datainkback.service.impl.ChatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//
//@Controller
//public class ChatController {
//
//    private final ChatService chatService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
//        this.chatService = chatService;
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage message, @AuthenticationPrincipal CustomUserDetails userDetails) {
//        // JWT에서 인증된 사용자 정보 추출
//        String senderId = userDetails.getUser().getId(); // 또는 필요에 따라 ID를 가져오는 로직 적용
//        message.setSenderId(senderId);
//
//        chatService.saveMessageToRedis(message);
//        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
//    }
//}
