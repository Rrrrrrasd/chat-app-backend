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
    public ResponseEntity<String> addFriend(@RequestBody FriendshipModel friendship) {
        friendshipService.addFriend(friendship);
        return ResponseEntity.ok("Friend request sent");
    }

    // 2. 친구 상태 업데이트 (ACCEPTED / REJECTED)
    @PutMapping("/")
    public ResponseEntity<String> updateFriendship(@RequestBody FriendshipModel friendship) {
        friendshipService.updateFriendship(friendship);
        return ResponseEntity.ok("Friendship updated");
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
    public ResponseEntity<String> blockFriend(@RequestParam Long targetId) {
        friendshipService.blockFriendSecure(targetId);
        return ResponseEntity.ok("차단 완료");
    }

    // 7. 차단 해제
    @PutMapping("/unblock")
    public ResponseEntity<String> unblockFriend(@RequestParam Long targetId) {
        friendshipService.unblockFriendSecure(targetId);
        return ResponseEntity.ok("차단 해제 완료");
    }

    @PostMapping("/by-nickname")
    public ResponseEntity<String> addFriendByNickname(@RequestParam String nickname) {
        String result = friendshipService.addFriendByNicknameUsingUsername(nickname);

        if (result.startsWith("친구 요청")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }


}