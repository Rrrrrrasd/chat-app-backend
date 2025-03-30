package com.example.backend.controller;


import com.example.backend.common.model.ChatMessageModel;
import com.example.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageModel message, Principal principal) {
        String username = principal.getName();
        chatService.handleWebSocketMessage(message, username, messagingTemplate);
    }
}
