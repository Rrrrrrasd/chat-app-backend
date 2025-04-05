package com.example.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 접속할 WebSocket 엔드포인트
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins("http://localhost:3000") // React 등 프론트
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트 -> 서버 : /app
        config.setApplicationDestinationPrefixes("/app");
        // 서버 -> 클라이언트 : /topic, /queue
        config.enableSimpleBroker("/topic", "/queue");
    }
}
