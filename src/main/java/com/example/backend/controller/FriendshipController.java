package com.example.backend.controller;

import com.example.backend.common.model.FriendshipModel;
import com.example.backend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    // 친구 요청 추가 (초기 상태: "PENDING")
    @PostMapping("/")
    public ResponseEntity<String> addFriend(@RequestBody FriendshipModel friendship) {
        friendshipService.addFriend(friendship);
        return ResponseEntity.ok("Friend request sent");
    }

    // 친구 요청 상태 업데이트 (예: 승인, 거절 등)
    @PutMapping("/")
    public ResponseEntity<String> updateFriendship(@RequestBody FriendshipModel friendship) {
        friendshipService.updateFriendship(friendship);
        return ResponseEntity.ok("Friendship updated");
    }
}