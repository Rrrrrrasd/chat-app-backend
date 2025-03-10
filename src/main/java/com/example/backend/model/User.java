package com.example.backend.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;  // 암호화된 비밀번호 저장
    private String nickname;
    private String statusMessage;
    private String profileImage;
}