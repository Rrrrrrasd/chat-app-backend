package com.example.backend.common.model;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ChatMessageModel {
    private Long id;
    private Long chatRoomId;  // 어떤 채팅방인가 (FK: chat_rooms.id)
    private Long senderId;    // 보낸 사람 (FK: users.id)
    private String message;   // 메시지 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private String nickname;// db 저장에 사용되는것 x -> 메시지 브로드캐스트 용도로 사용
}
