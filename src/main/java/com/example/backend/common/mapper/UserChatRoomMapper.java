package com.example.backend.common.mapper;

import com.example.backend.common.model.UserChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserChatRoomMapper {
    void insertUserChatRoom(UserChatRoom userChatRoom);
    List<UserChatRoom> selectChatRoomsByUserId(@Param("userId") Long userId);
    List<UserChatRoom> selectUsersByRoomId(@Param("chatRoomId") Long chatRoomId);
}
