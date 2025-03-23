package com.example.backend.controller;

import com.example.backend.common.model.ChatMessage;
import com.example.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    // 클라이언트에서 "/app/chat.sendMessage"로 보낸 메시지 처리
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    // (예) 모든 구독자에게 방송. 방별로 구독하려면 /topic/room.{roomId} 형식 사용
    public ChatMessage sendMessage(ChatMessage message) {
        // DB 저장
        ChatMessage saved = chatService.saveMessage(
                message.getChatRoomId(),
                message.getSenderId(),
                message.getMessage()
        );
        return saved; // 이 값이 /topic/public 구독자들에게 전달됨
    }
}
