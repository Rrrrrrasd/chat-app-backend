package com.example.backend.service;

import com.example.backend.common.exception.CustomException;
import com.example.backend.common.exception.CustomExceptionEnum;
import com.example.backend.common.mapper.ChatMessageMapper;
import com.example.backend.common.mapper.ChatRoomMapper;
import com.example.backend.common.mapper.UserChatRoomMapper;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.ChatMessageModel;
import com.example.backend.common.model.ChatRoomModel;
import com.example.backend.common.model.UserChatRoomModel;
import com.example.backend.common.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserChatRoomMapper userChatRoomMapper;
    private final UserMapper userMapper;

    //채팅방 생성
    public ChatRoomModel createChatRoom(String name, Long createdBy) {
        ChatRoomModel room = new ChatRoomModel();
        room.setName(name);
        room.setCreatedBy(createdBy);
        LocalDateTime now = LocalDateTime.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);

        chatRoomMapper.insertChatRoom(room);
        return room;
    }


    //채팅방 참여
    public void joinChatRoom(Long userId, Long chatRoomId) {
        UserChatRoomModel userChatRoomModel = new UserChatRoomModel();
        userChatRoomModel.setUserId(userId);
        userChatRoomModel.setChatRoomId(chatRoomId);
        LocalDateTime now = LocalDateTime.now();
        userChatRoomModel.setJoinedAt(now);
        userChatRoomModel.setCreatedAt(now);
        userChatRoomModel.setUpdatedAt(now);
        userChatRoomMapper.insertUserChatRoom(userChatRoomModel);
    }


    //채팅 메시지 저장
    public ChatMessageModel saveMessage(Long chatRoomId, Long senderId, String message) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setChatRoomId(chatRoomId);
        chatMessageModel.setSenderId(senderId);
        chatMessageModel.setMessage(message);
        LocalDateTime now = LocalDateTime.now();
        chatMessageModel.setCreatedAt(now);
        chatMessageModel.setUpdatedAt(now);

        chatMessageMapper.insertChatMessage(chatMessageModel);
        return chatMessageModel;
    }

    //특정 채팅방 메시지 이력 조회
    public List<ChatMessageModel> getMessages(Long chatRoomId) {
        List<ChatMessageModel> messages = chatMessageMapper.selectMessagesByRoomId(chatRoomId);
        for (ChatMessageModel msg : messages) {
            UserModel sender = userMapper.selectUserById(msg.getSenderId());
            msg.setNickname(sender.getNickname());
        }
        return messages;
    }

    //전체 채팅방 목록 조회
    public List<ChatRoomModel> getAllChatRooms() {
        return chatRoomMapper.selectAllChatRooms();
    }

    //특정 채팅방 정보
    public ChatRoomModel getChatRoom(Long roomId) {

        ChatRoomModel room = chatRoomMapper.selectChatRoomById(roomId);
        if (room == null) {
            throw new CustomException(CustomExceptionEnum.CHAT_ROOM_NOT_FOUND);
        }
        return room;
    }

    // 사용자가 가입한 채팅방 목록 조회
    public List<ChatRoomModel> getChatRoomsByUserId(Long userId) {
        List<UserChatRoomModel> userChatRooms = userChatRoomMapper.selectChatRoomsByUserId(userId);
        List<ChatRoomModel> chatRooms = new ArrayList<>();
        for (UserChatRoomModel ucr : userChatRooms) {
            ChatRoomModel room = chatRoomMapper.selectChatRoomById(ucr.getChatRoomId());
            chatRooms.add(room);
        }
        return chatRooms;
    }

    public void leaveChatRoom(Long userId, Long chatRoomId) {
        userChatRoomMapper.deleteUserChatRoom(userId, chatRoomId);
    }

}
