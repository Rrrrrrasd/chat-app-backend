package com.example.backend.common.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendshipModel {
    private Long id;
    private Long userId;      // FK: users.id
    private Long friendId;    // FK: users.id
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
