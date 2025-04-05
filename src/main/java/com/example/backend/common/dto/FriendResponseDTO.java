package com.example.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponseDTO {
    private Long id;         // FRIENDSHIP ID
    private Long friendId;   // 친구의 ID
    private String nickname; // 친구의 닉네임
}
