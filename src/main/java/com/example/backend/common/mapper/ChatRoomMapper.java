package com.example.backend.common.mapper;

import com.example.backend.common.model.ChatRoomModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    void insertChatRoom(ChatRoomModel chatRoom);
    ChatRoomModel selectChatRoomById(@Param("id") Long id);
    List<ChatRoomModel> selectAllChatRooms();
    int updateChatRoom(ChatRoomModel chatRoom);
}
