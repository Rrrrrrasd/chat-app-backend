package com.example.backend.common.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomModel {
    private Long id;
    private String name;
    private String roomType; // 예: "PUBLIC", "PRIVATE"
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
