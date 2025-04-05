package com.example.backend.common.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileImageModel {
    private Long id;
    private Long userId;
    private String profileImage; // 프로필 이미지 URL 또는 경로
    private LocalDateTime updatedAt; // 이미지 변경 시각
}
