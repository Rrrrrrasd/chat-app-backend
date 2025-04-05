package com.example.backend.controller;

import com.example.backend.common.model.NotificationModel;
import com.example.backend.service.NotificationService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    // 현재 로그인 사용자의 알림 목록 조회
    @GetMapping
    public ResponseEntity<List<NotificationModel>> getNotifications() {
        Long userId = userService.getCurrentAuthenticatedUser().getId();
        List<NotificationModel> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // 개별 알림 읽음 처리
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
