package com.example.backend.common.mapper;


import com.example.backend.common.model.NotificationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationModel notification);
    List<NotificationModel> selectNotificationsByUserId(@Param("userId") Long userId);
    int updateNotification(NotificationModel notification);
    int deleteNotification(@Param("id") Long id);
}
