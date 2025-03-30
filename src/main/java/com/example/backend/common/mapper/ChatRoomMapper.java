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
    ChatRoomModel selectPrivateRoomByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    void softDeletePrivateChatRoomById(@Param("chatRoomId") Long chatRoomId,
                                       @Param("deletedAt") String deletedAt);

    // 채팅방 복원: deleted_at을 null로 업데이트
    void restorePrivateChatRoomById(@Param("chatRoomId") Long chatRoomId);
    int updateChatRoom(ChatRoomModel chatRoom);
}
