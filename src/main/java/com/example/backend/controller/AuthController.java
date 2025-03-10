package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.SignupRequest;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원가입 엔드포인트
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequest signupRequest) {
        // 이미 등록된 아이디인지 확인
        User existingUser = userMapper.selectUserByUsername(signupRequest.getUsername());
        if (existingUser != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미 사용 중인 아이디입니다.");
        }
        // 비밀번호 암호화 및 사용자 생성
        User newUser = new User();
        newUser.setUsername(signupRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setNickname(signupRequest.getNickname());
        userMapper.insertUser(newUser);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    /**
     * 로그인 엔드포인트
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        User user = userMapper.selectUserByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 혹은 비밀번호가 올바르지 않습니다.");
        }
        // 로그인 성공 시 JWT access, refresh 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }
}
