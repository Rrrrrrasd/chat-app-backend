package com.example.backend.controller;


import com.example.backend.common.model.ChatMessageModel;
import com.example.backend.common.model.ChatRoomModel;
import com.example.backend.common.model.UserModel;
import com.example.backend.service.ChatService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    // 채팅방 생성
    // PUBLIC 채팅방 생성
    @PostMapping("/rooms")
    public ChatRoomModel createRoom(@RequestParam String name,
                                    @RequestParam Long createdBy) {
        return chatService.createChatRoom(name, createdBy);
    }

    // 친구간 1:1 채팅 (PRIVATE 채팅방 생성)
    @PostMapping("/private/start")
    public ResponseEntity<Long> startPrivateChat(@RequestBody Map<String, Long> body) {
        Long friendId = body.get("friendId");
        ChatRoomModel room = chatService.createPrivateChatRoom(getCurrentUserId(), friendId);
        return ResponseEntity.ok(room.getId());
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoomModel> getAllChatRooms() {
        return chatService.getAllChatRooms();
    }

    //특정 채팅방 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageModel> getMessages(@PathVariable Long roomId) {
        return chatService.getMessages(roomId);
    }

    //채팅방 참여
    @PostMapping("/rooms/{roomId}/join")
    public void joinRoom(@PathVariable Long roomId) {
        chatService.joinChatRoomWithAuthentication(roomId);
    }

    // 신규 엔드포인트: 현재 사용자가 가입한 채팅방 목록 조회
    // 내가 참여한 채팅방 목록
    @GetMapping("/myrooms")
    public List<ChatRoomModel> getMyChatRooms() {
        return chatService.getMyRoomsWithAuthentication();
    }

    //채팅방에서 퇴장
    @DeleteMapping("/rooms/{roomId}/leave")
    public void leaveRoom(@PathVariable Long roomId) {
        chatService.leaveChatRoomWithAuthentication(roomId);
    }

    // 메시지 읽음 처리 엔드포인트 예시
    @PostMapping("/messages/{messageId}/read")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatService.markMessageAsRead(messageId, getCurrentUserId());
    }

    // 현재 인증된 사용자의 ID를 반환하는 헬퍼 메서드
    private Long getCurrentUserId() {
        UserModel user = userService.getCurrentAuthenticatedUser();
        return user.getId();

    }
}
