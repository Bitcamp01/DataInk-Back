package com.bit.datainkback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // 메시지를 브로드캐스트할 때 사용
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트에서 메시지를 보낼 때 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")  // 클라이언트가 연결할 엔드포인트
                .setAllowedOrigins("*")  // 허용할 오리진 설정
                .withSockJS();  // SockJS 지원
    }
}
