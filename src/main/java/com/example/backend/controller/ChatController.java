package com.example.backend.controller;

import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.ChatMessageModel;
import com.example.backend.common.model.ChatRoomModel;
import com.example.backend.common.model.UserModel;
import com.example.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserMapper userMapper;

    // 채팅방 생성
    // name : 채팅방 이름
    // createBy: 방생성자 (userId)
    @PostMapping("/rooms")
    public ChatRoomModel createRoom(@RequestParam String name,
                                    @RequestParam Long createdBy) {
        return chatService.createChatRoom(name, createdBy);
    }
    
    //친구간 1:1 채팅
    @PostMapping("/private/start")
    public ResponseEntity<Long> startPrivateChat(@RequestBody Map<String, Long> body) {
        Long friendId = body.get("friendId");
        ChatRoomModel room = chatService.startPrivateChatWithAuthentication(friendId);
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
    public String joinRoom(@PathVariable Long roomId) {
        chatService.joinChatRoomWithAuthentication(roomId);
        return "채팅방 참여 성공";
    }

    // 신규 엔드포인트: 현재 사용자가 가입한 채팅방 목록 조회
    // 내가 참여한 채팅방 목록
    @GetMapping("/myrooms")
    public List<ChatRoomModel> getMyChatRooms() {
        return chatService.getMyRoomsWithAuthentication();
    }

    //채팅방에서 퇴장
    @DeleteMapping("/rooms/{roomId}/leave")
    public String leaveRoom(@PathVariable Long roomId) {
        chatService.leaveChatRoomWithAuthentication(roomId);
        return "채팅방 퇴장 완료";
    }
}
