package com.example.backend.controller;


import com.example.backend.common.dto.LoginRequestDTO;
import com.example.backend.common.dto.SignupRequestDTO;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequestDTO signupRequestdto) {
        return userService.registerUser(signupRequestdto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestdto) {
        return userService.login(loginRequestdto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        return userService.refreshAccessToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return userService.logout();
    }
}
