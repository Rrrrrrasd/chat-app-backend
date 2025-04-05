package com.example.backend.common.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserStatusModel {
    private Long id;
    private Long userId;
    private String statusMessage;
    private LocalDateTime updatedAt; // 상태 메시지 변경 시각
}