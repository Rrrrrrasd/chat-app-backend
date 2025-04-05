package com.example.backend.controller;

import com.example.backend.common.model.UserProfileImageModel;
import com.example.backend.service.UserProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profile-image")
@RequiredArgsConstructor
public class UserProfileImageController {

    private final UserProfileImageService userProfileImageService;

    // 특정 사용자의 프로필 이미지 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileImageModel> getUserProfileImage(@PathVariable Long userId) {
        UserProfileImageModel profileImage = userProfileImageService.getUserProfileImageByUserId(userId);
        return ResponseEntity.ok(profileImage);
    }

    // 특정 사용자의 프로필 이미지 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileImageModel> updateUserProfileImage(@PathVariable Long userId,
                                                                        @RequestBody UserProfileImageModel profileImage) {
        profileImage.setUserId(userId);
        userProfileImageService.updateUserProfileImage(profileImage);
        UserProfileImageModel updatedImage = userProfileImageService.getUserProfileImageByUserId(userId);
        return ResponseEntity.ok(updatedImage);
    }
}
