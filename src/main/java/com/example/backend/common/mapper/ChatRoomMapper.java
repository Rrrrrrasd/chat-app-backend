package com.example.backend.common.mapper;

import com.example.backend.common.model.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    void insertChatRoom(ChatRoom chatRoom);
    ChatRoom selectChatRoomById(@Param("id") Long id);
    List<ChatRoom> selectAllChatRooms();
}
