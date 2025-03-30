package com.example.backend.service;

import com.example.backend.common.mapper.FriendshipMapper;
import com.example.backend.common.model.FriendshipModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipMapper friendshipMapper;

    public void addFriend(FriendshipModel friendship) {
        LocalDateTime now = LocalDateTime.now();
        friendship.setCreatedAt(now);
        friendship.setUpdatedAt(now);
        friendship.setStatus("PENDING");  // 초기 상태 설정
        friendshipMapper.insertFriendship(friendship);
    }

    public void updateFriendship(FriendshipModel friendship) {
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipMapper.updateFriendship(friendship);
    }
}
