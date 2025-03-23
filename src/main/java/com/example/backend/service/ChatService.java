package com.example.backend.service;

import com.example.backend.common.mapper.ChatMessageMapper;
import com.example.backend.common.mapper.ChatRoomMapper;
import com.example.backend.common.mapper.UserChatRoomMapper;
import com.example.backend.common.model.ChatMessage;
import com.example.backend.common.model.ChatRoom;
import com.example.backend.common.model.UserChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserChatRoomMapper userChatRoomMapper;

    //채팅방 생성
    public ChatRoom createChatRoom(String name, Long createdBy) {
        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setCreatedBy(createdBy);
        room.setCreatedAt(LocalDateTime.now());

        chatRoomMapper.insertChatRoom(room);
        return room;
    }


    //채팅방 참여
    public void joinChatRoom(Long userId, Long chatRoomId) {
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.setUserId(userId);
        userChatRoom.setChatRoomId(chatRoomId);
        userChatRoom.setJoinedAt(LocalDateTime.now());
        userChatRoomMapper.insertUserChatRoom(userChatRoom);
    }


    //채팅 메시지 저장
    public ChatMessage saveMessage(Long chatRoomId, Long senderId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setSenderId(senderId);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessageMapper.insertChatMessage(chatMessage);
        return chatMessage;
    }

    //특정 채팅방 메시지 이력 조회
    public List<ChatMessage> getMessages(Long chatRoomId) {
        return chatMessageMapper.selectMessagesByRoomId(chatRoomId);
    }

    //전체 채팅방 목록 조회
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomMapper.selectAllChatRooms();
    }

    //특정 채팅방 정보
    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomMapper.selectChatRoomById(roomId);
    }
}
