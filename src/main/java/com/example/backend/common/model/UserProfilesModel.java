package com.example.backend.common.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfilesModel {
    private Long id;
    private Long userId;
    private String statusMessage;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
