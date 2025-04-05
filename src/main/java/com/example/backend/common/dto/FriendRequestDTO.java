package com.example.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendRequestDTO {
    private Long id;         // FRIENDSHIP ID
    private Long requesterId;
    private String nickname; // 요청 보낸 사람의 닉네임
}