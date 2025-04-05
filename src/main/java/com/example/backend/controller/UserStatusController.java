package com.example.backend.controller;

import com.example.backend.common.model.UserStatusModel;
import com.example.backend.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-status")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 특정 사용자의 상태 메시지 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserStatusModel> getUserStatus(@PathVariable Long userId) {
        UserStatusModel status = userStatusService.getUserStatusByUserId(userId);
        return ResponseEntity.ok(status);
    }

    // 특정 사용자의 상태 메시지 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserStatusModel> updateUserStatus(@PathVariable Long userId,
                                                            @RequestBody UserStatusModel userStatus) {
        userStatus.setUserId(userId);
        userStatusService.updateUserStatus(userStatus);
        UserStatusModel updatedStatus = userStatusService.getUserStatusByUserId(userId);
        return ResponseEntity.ok(updatedStatus);
    }
}
