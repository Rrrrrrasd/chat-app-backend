package com.example.backend.service;

import com.example.backend.common.mapper.NotificationMapper;
import com.example.backend.common.model.NotificationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    // 알림 생성
    public void createNotification(Long userId, String type, String message) {
        NotificationModel notification = new NotificationModel();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        System.out.println("NotificationService.createNotification 호출: " + notification);
        notificationMapper.insertNotification(notification);
    }

    // 현재 사용자의 알림 목록 조회
    public List<NotificationModel> getNotificationsForUser(Long userId) {
        return notificationMapper.selectNotificationsByUserId(userId);
    }

    // 특정 알림을 읽음 처리
    public void markNotificationAsRead(Long notificationId) {
        NotificationModel notification = new NotificationModel();
        notification.setId(notificationId);
        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationMapper.updateNotification(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }
}
