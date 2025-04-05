package com.example.backend.controller;

import com.example.backend.common.dto.FriendRequestDTO;
import com.example.backend.common.dto.FriendResponseDTO;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.FriendshipModel;
import com.example.backend.common.model.UserModel;
import com.example.backend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserMapper userMapper;

    // 1. 친구 요청 추가 (PENDING)
    @PostMapping("/")
    public void addFriend(@RequestBody FriendshipModel friendship) {
        friendshipService.addFriend(friendship);
    }

    // 2. 친구 상태 업데이트 (ACCEPTED / REJECTED)
    @PutMapping("/")
    public void updateFriendship(@RequestBody FriendshipModel friendship) {
        friendshipService.updateFriendship(friendship);
    }

    // 3. 나의 친구 목록 조회 (ACCEPTED만)
    @GetMapping("/my")
    public ResponseEntity<List<FriendResponseDTO>> getMyFriends() {
        return ResponseEntity.ok(friendshipService.getAcceptedFriendNicknamesSecure());
    }

    // 4. 친구 요청(PENDING) 받은 목록
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests() {
        return ResponseEntity.ok(friendshipService.getPendingRequestDTOsSecure());
    }

    // 5. 차단 목록 조회
    @GetMapping("/blocked")
    public ResponseEntity<List<FriendResponseDTO>> getBlockedUsers() {
        return ResponseEntity.ok(friendshipService.getBlockedFriendNicknamesSecure());
    }

    // 6. 차단
    @PutMapping("/block")
    public void blockFriend(@RequestParam Long targetId) {
        friendshipService.blockFriendSecure(targetId);
    }

    // 7. 차단 해제
    @PutMapping("/unblock")
    public void unblockFriend(@RequestParam Long targetId) {
        friendshipService.unblockFriendSecure(targetId);
    }

    @PostMapping("/by-nickname")
    public void addFriendByNickname(@RequestParam String nickname) {
        friendshipService.addFriendByNicknameUsingUsername(nickname);
    }
}