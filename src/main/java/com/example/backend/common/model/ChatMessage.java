package com.example.backend.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long chatRoomId;  // 어떤 채팅방인가 (FK: chat_rooms.id)
    private Long senderId;    // 보낸 사람 (FK: users.id)
    private String message;   // 메시지 내용
    private LocalDateTime timestamp; // 메시지 전송 시각
}
