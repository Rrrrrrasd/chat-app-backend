package com.example.backend.common.model;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class UserChatRoomModel {
    private Long id;
    private Long userId;      // 참여한 유저 (FK: users.id)
    private Long chatRoomId;  // 참여한 채팅방 (FK: chat_rooms.id)
    private LocalDateTime joinedAt; // 참여한 시각
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}