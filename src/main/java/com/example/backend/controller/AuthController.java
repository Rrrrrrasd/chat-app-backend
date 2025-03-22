package com.example.backend.controller;


import com.example.backend.common.dto.AuthResponseDTO;
import com.example.backend.common.dto.LoginRequestDTO;
import com.example.backend.common.dto.SignupRequestDTO;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.UserModel;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    //정상작동하는지 확인하기위해 임시로 만든것 -> 이후 삭제
    @GetMapping("/me")
    public ResponseEntity<UserModel> getCurrentUser(HttpServletRequest request) {
        // SecurityContextHolder를 이용하여 현재 인증된 사용자 정보를 가져온다고 가정
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserModel user = userMapper.selectUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup")
    public String  registerUser(@RequestBody @Valid SignupRequestDTO signupRequestdto) {
        return userService.registerUser(signupRequestdto);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO loginRequestdto, HttpServletResponse response) {
        String result = userService.login(loginRequestdto, response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refreshAccessToken(HttpServletRequest request) {
        return userService.refreshAccessToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        String result = userService.logout(response);
        return ResponseEntity.ok(result);
    }
}
