package com.example.backend.service;

import com.example.backend.common.mapper.UserProfileMapper;
import com.example.backend.common.model.UserProfilesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfilesModel getUserProfileByUserId(Long userId) {
        return userProfileMapper.selectUserProfileByUserId(userId);
    }

    public UserProfilesModel createUserProfile(UserProfilesModel userProfile) {
        LocalDateTime now = LocalDateTime.now();
        userProfile.setCreatedAt(now);
        userProfile.setUpdatedAt(now);
        userProfileMapper.insertUserProfile(userProfile);
        return userProfile;
    }

    public UserProfilesModel updateUserProfile(UserProfilesModel userProfile) {
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfileMapper.updateUserProfile(userProfile);
        return userProfile;
    }
}
