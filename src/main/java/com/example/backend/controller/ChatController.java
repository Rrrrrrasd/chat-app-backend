package com.example.backend.controller;

import com.example.backend.common.model.ChatMessage;
import com.example.backend.common.model.ChatRoom;
import com.example.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성
    // name : 채팅방 이름
    // createBy: 방생성자 (userId)
    @PostMapping("/rooms")
    public ChatRoom createRoom(@RequestParam String name,
                               @RequestParam Long createdBy) {
        return chatService.createChatRoom(name, createdBy);
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> getAllChatRooms() {
        return chatService.getAllChatRooms();
    }

    //특정 채팅방 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long roomId) {
        return chatService.getMessages(roomId);
    }

    //채팅방 참여
    @PostMapping("/rooms/{roomId}/join")
    public String joinRoom(@PathVariable Long roomId,
                           @RequestParam Long userId) {
        chatService.joinChatRoom(userId, roomId);
        return "채팅방 참여 성공";
    }
}
