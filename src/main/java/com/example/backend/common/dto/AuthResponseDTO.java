package com.example.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long exp;
}