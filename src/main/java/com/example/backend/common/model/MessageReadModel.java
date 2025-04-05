package com.example.backend.common.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageReadModel {
    private Long id;
    private Long messageId;
    private Long userId;
    private LocalDateTime readAt;
}
