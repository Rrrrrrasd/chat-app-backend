package com.example.backend.common.model;

import lombok.Data;
import lombok.Getter;


import java.time.LocalDateTime;

@Data
@Getter
public class ChatRoomModel {
    private Long id;
    private String name;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
