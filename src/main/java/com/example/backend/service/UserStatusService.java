package com.example.backend.service;

import com.example.backend.common.mapper.UserStatusMapper;
import com.example.backend.common.model.UserStatusModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusMapper userStatusMapper;

    public void createUserStatus(UserStatusModel userStatus) {
        userStatusMapper.insertUserStatus(userStatus);
    }

    public UserStatusModel getUserStatusByUserId(Long userId) {
        return userStatusMapper.selectUserStatusByUserId(userId);
    }

    public int updateUserStatus(UserStatusModel userStatus) {
        userStatus.setUpdatedAt(LocalDateTime.now());
        return userStatusMapper.updateUserStatus(userStatus);
    }
}
