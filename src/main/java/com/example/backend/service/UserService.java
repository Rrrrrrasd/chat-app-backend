package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void registerUser(User user) {
        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
    }

    public String login(User loginRequest) {
        // 사용자 조회 후 비밀번호 확인, JWT 토큰 발급
        User user = userMapper.selectUserByUsername(loginRequest.getUsername());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // JWT 유틸리티 클래스를 활용하여 토큰 생성 (추후 구현)
            return "dummy-jwt-token"; // 실제 JWT 토큰 발급 코드로 교체
        }
        throw new RuntimeException("로그인 실패: 사용자 정보가 올바르지 않음");
    }
}
