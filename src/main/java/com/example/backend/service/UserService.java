package com.example.backend.service;

import com.example.backend.common.dto.AuthResponseDTO;
import com.example.backend.common.exception.CustomException;
import com.example.backend.common.exception.CustomExceptionEnum;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.UserModel;
import com.example.backend.common.model.UserProfilesModel;
import com.example.backend.common.util.JwtUtil;
import com.example.backend.common.dto.LoginRequestDTO;
import com.example.backend.common.dto.SignupRequestDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;

    public String  registerUser(SignupRequestDTO signupRequestDTO) {
        // 아이디 중복 체크
        UserModel existingUser = userMapper.selectUserByUsername(signupRequestDTO.getUsername());
        if (existingUser != null) {
            throw new CustomException(CustomExceptionEnum.USERNAME_ALREADY_EXISTS);
        }

        // 닉네임 중복 체크
        boolean nicknameExists = userMapper.existsByNickname(signupRequestDTO.getNickname());
        if (nicknameExists) {
            throw new CustomException(CustomExceptionEnum.DUPLICATE_NICKNAME);
        }

        // 비밀번호 암호화 및 사용자 생성
        UserModel newUser = new UserModel();
        newUser.setUsername(signupRequestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        newUser.setNickname(signupRequestDTO.getNickname());
        userMapper.insertUser(newUser);

        // 신규 사용자 등록 후 기본 사용자 프로필 생성
        UserProfilesModel userProfile = new UserProfilesModel();
        userProfile.setUserId(newUser.getId());
        userProfile.setStatusMessage("");
        userProfile.setProfileImage("");
        userProfileService.createUserProfile(userProfile);

        return "회원가입이 성공적으로 완료되었습니다.";
    }

    public String login(LoginRequestDTO loginRequestdto, HttpServletResponse response) {
        UserModel user = userMapper.selectUserByUsername(loginRequestdto.getUsername());

        if (user == null || !passwordEncoder.matches(loginRequestdto.getPassword(), user.getPassword())) {
            throw new CustomException(CustomExceptionEnum.INVALID_CREDENTIALS);
        }
        // 로그인 성공 시 JWT access, refresh 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Access Token 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true); // 클라이언트 스크립트 접근 불가
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(900);
        // accessTokenCookie.setSecure(true);
        response.addCookie(accessTokenCookie);

        // Refresh Token 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 3600);
        // refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);


        return "로그인 성공";
    }

    public AuthResponseDTO refreshAccessToken(HttpServletRequest request) {
        
        //쿠키에서 리프레시 토큰 가져오기
        String refreshToken = null;
        if(WebUtils.getCookie(request, "refreshToken") != null) {
            refreshToken = WebUtils.getCookie(request, "refreshToken").getValue();
        }

        if (refreshToken == null) {
            throw new CustomException(CustomExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        jwtUtil.validateToken(refreshToken);

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(newAccessToken);
        authResponseDTO.setRefreshToken(newRefreshToken);
        authResponseDTO.setExp(jwtUtil.getExpiration(newAccessToken));

        return authResponseDTO;
    }


    public String  logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);  // 즉시 삭제
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
        return "로그아웃 성공";
    }

    public UserModel getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(CustomExceptionEnum.UNAUTHORIZED);
        }

        String username = (String) authentication.getPrincipal();
        UserModel user = userMapper.selectUserByUsername(username);
        if (user == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        return user;
    }




}
