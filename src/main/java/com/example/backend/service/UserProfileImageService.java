package com.example.backend.service;

import com.example.backend.common.mapper.UserProfileImageMapper;
import com.example.backend.common.model.UserProfileImageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileImageService {

    private final UserProfileImageMapper userProfileImageMapper;

    public void createUserProfileImage(UserProfileImageModel userProfileImage) {
        userProfileImageMapper.insertUserProfileImage(userProfileImage);
    }

    public UserProfileImageModel getUserProfileImageByUserId(Long userId) {
        return userProfileImageMapper.selectUserProfileImageByUserId(userId);
    }

    public int updateUserProfileImage(UserProfileImageModel userProfileImage) {
        userProfileImage.setUpdatedAt(LocalDateTime.now());
        return userProfileImageMapper.updateUserProfileImage(userProfileImage);
    }
}
