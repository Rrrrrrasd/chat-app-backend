package com.example.backend.service;

import com.example.backend.common.dto.AuthResponseDTO;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.UserModel;
import com.example.backend.common.util.JwtUtil;

import com.example.backend.common.dto.LoginRequestDTO;
import com.example.backend.common.dto.SignupRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerUser(SignupRequestDTO signupRequestDTO) {
        // 이미 등록된 아이디인지 확인
        UserModel existingUser = userMapper.selectUserByUsername(signupRequestDTO.getUsername());

        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 아이디입니다.");
        }
        // 비밀번호 암호화 및 사용자 생성
        UserModel newUser = new UserModel();
        newUser.setUsername(signupRequestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        newUser.setNickname(signupRequestDTO.getNickname());
        userMapper.insertUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입이 성공적으로 완료되었습니다."));
    }

    public ResponseEntity<?> login(LoginRequestDTO loginRequestdto) {
        UserModel user = userMapper.selectUserByUsername(loginRequestdto.getUsername());

        if (user == null || !passwordEncoder.matches(loginRequestdto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "아이디 혹은 비밀번호가 올바르지 않습니다."));
        }
        // 로그인 성공 시 JWT access, refresh 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(accessToken);


        return ResponseEntity.ok()
                .headers(headers)
                .body(authResponseDTO);
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        
        //쿠키에서 리프레시 토큰 가져오기
        String refreshToken = null;
        if(WebUtils.getCookie(request, "refreshToken") != null) {
            refreshToken = WebUtils.getCookie(request, "refreshToken").getValue();
        }

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh Token이 유효하지 않습니다."));
        }


        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(newAccessToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body(authResponseDTO);
    }


    public ResponseEntity<?> logout() {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return  ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("message", "로그아웃 성공"));
    }

}
