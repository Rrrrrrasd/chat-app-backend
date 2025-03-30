package com.example.backend.common.mapper;

import com.example.backend.common.model.UserChatRoomModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserChatRoomMapper {
    void insertUserChatRoom(UserChatRoomModel userChatRoom);
    List<UserChatRoomModel> selectChatRoomsByUserId(@Param("userId") Long userId);
    List<UserChatRoomModel> selectUsersByRoomId(@Param("chatRoomId") Long chatRoomId);
    int updateUserChatRoom(UserChatRoomModel userChatRoom);
    int deleteUserChatRoom(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);
}
