package com.example.backend.common.mapper;

import com.example.backend.common.model.ChatRoomModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatRoomMapper {


    void insertChatRoom(ChatRoomModel chatRoom);


    ChatRoomModel selectChatRoomById(@Param("id") Long id);


    List<ChatRoomModel> selectAllChatRooms();


    List<ChatRoomModel> selectChatRoomsByType(@Param("roomType") String roomType);



    ChatRoomModel selectPrivateRoomByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);


    void softDeletePrivateChatRoomById(@Param("chatRoomId") Long chatRoomId, @Param("deletedAt") String deletedAt);


    void restorePrivateChatRoomById(@Param("chatRoomId") Long chatRoomId);


    int updateChatRoom(ChatRoomModel chatRoom);
}
