package com.example.backend.common.mapper;

import com.example.backend.common.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    void insertChatMessage(ChatMessage chatMessage);
    List<ChatMessage> selectMessagesByRoomId(@Param("chatRoomId") Long chatRoomId);
}
