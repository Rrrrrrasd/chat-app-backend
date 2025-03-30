package com.example.backend.controller;

import com.example.backend.common.model.UserProfilesModel;
import com.example.backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // 특정 사용자의 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfilesModel> getUserProfile(@PathVariable Long userId) {
        UserProfilesModel profile = userProfileService.getUserProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    // 사용자 프로필 생성
    @PostMapping("/")
    public ResponseEntity<UserProfilesModel> createUserProfile(@RequestBody UserProfilesModel userProfile) {
        UserProfilesModel createdProfile = userProfileService.createUserProfile(userProfile);
        return ResponseEntity.ok(createdProfile);
    }

    // 사용자 프로필 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserProfilesModel> updateUserProfile(@PathVariable Long id, @RequestBody UserProfilesModel userProfile) {
        userProfile.setId(id);
        UserProfilesModel updatedProfile = userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.ok(updatedProfile);
    }
}
