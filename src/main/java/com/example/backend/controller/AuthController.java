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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;



    @GetMapping("/me")
    public ResponseEntity<UserModel> getCurrentUser() {
        UserModel user = userService.getCurrentAuthenticatedUser();
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
