package com.example.backend.common.mapper;

import com.example.backend.common.model.ChatMessageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    void insertChatMessage(ChatMessageModel chatMessage);
    List<ChatMessageModel> selectMessagesByRoomId(@Param("chatRoomId") Long chatRoomId);
    int updateChatMessage(ChatMessageModel chatMessage);
}
