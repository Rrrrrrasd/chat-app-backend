package com.example.backend.common.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationModel {
    private Long id;
    private Long userId;
    private String type;       // 예: FRIEND_REQUEST, MESSAGE 등
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
