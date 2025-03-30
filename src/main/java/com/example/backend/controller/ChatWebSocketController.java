package com.example.backend.controller;

import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.ChatMessageModel;
import com.example.backend.common.model.UserModel;
import com.example.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserMapper userMapper;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageModel message, Principal principal) {
        System.out.println("🔔 WebSocket 메시지 수신됨: " + message);

        //현재 사용자 정보 가져오기 (JWT 기반)
        String username = principal.getName();
        UserModel sender = userMapper.selectUserByUsername(username);

        //메시지 저장
        ChatMessageModel saved = chatService.saveMessage(
                message.getChatRoomId(),
                sender.getId(), // 인증된 사용자 ID 사용
                message.getMessage()
        );

        saved.setNickname(sender.getNickname());

        //실시간 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/room." + message.getChatRoomId(),
                saved
        );
    }
}
