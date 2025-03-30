package com.example.backend.service;

import com.example.backend.common.exception.CustomException;
import com.example.backend.common.exception.CustomExceptionEnum;
import com.example.backend.common.mapper.*;
import com.example.backend.common.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FriendshipMapper friendshipMapper;

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
        ChatRoomModel room = chatRoomMapper.selectChatRoomById(chatRoomId);

        if (room.getDeletedAt() != null) {
            throw new CustomException(CustomExceptionEnum.CHAT_ROOM_BLOCKED);
        }

        // 1:1 채팅방 차단 검사
        if (room.getName().contains(" - ")) {
            List<UserChatRoomModel> participants = userChatRoomMapper.selectUsersByRoomId(chatRoomId);
            Long otherUserId = participants.stream()
                    .map(UserChatRoomModel::getUserId)
                    .filter(id -> !id.equals(userId))
                    .findFirst()
                    .orElse(null);

            if (otherUserId != null) {
                // 차단 관계 검사
                FriendshipModel blocked = friendshipMapper.selectBlockedRelation(userId, otherUserId);
                if (blocked != null) {
                    throw new CustomException(CustomExceptionEnum.BLOCKED_USER);
                }

                // 기존 친구 상태 검사도 유지
                FriendshipModel friendship = friendshipMapper.selectFriendship(userId, otherUserId);
                if (friendship == null || !"ACCEPTED".equals(friendship.getStatus())) {
                    friendship = friendshipMapper.selectFriendship(otherUserId, userId);
                }
                if (friendship == null || !"ACCEPTED".equals(friendship.getStatus())) {
                    throw new CustomException(CustomExceptionEnum.NOT_FRIEND);
                }
            }
        }

        // 정상 참여 로직
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
        List<ChatRoomModel> allRooms = chatRoomMapper.selectAllChatRooms();
        return allRooms.stream()
                .filter(room -> !room.getName().contains(" - ")) // 1:1 채팅방은 제외
                .toList();
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

    public ChatRoomModel createPrivateChatRoom(Long userId, Long friendId) {
        // 0. 차단 여부 검사
        FriendshipModel blockedRelation = friendshipMapper.selectBlockedRelation(userId, friendId);
        if (blockedRelation != null) {
            throw new CustomException(CustomExceptionEnum.BLOCKED_USER);
        }

        // 1. 친구 상태 확인
        FriendshipModel friendship = friendshipMapper.selectFriendship(userId, friendId);
        if (friendship == null) {
            friendship = friendshipMapper.selectFriendship(friendId, userId);
        }
        if (friendship == null || !"ACCEPTED".equals(friendship.getStatus())) {
            throw new CustomException(CustomExceptionEnum.NOT_FRIEND);
        }

        // 2. 기존 채팅방 있는지 확인
        ChatRoomModel existingRoom = chatRoomMapper.selectPrivateRoomByUserIds(userId, friendId);
        if (existingRoom != null) return existingRoom;

        // 3. 사용자 닉네임 조회
        UserModel me = userMapper.selectUserById(userId);
        UserModel friend = userMapper.selectUserById(friendId);
        String roomName = me.getNickname() + " - " + friend.getNickname();

        // 4. 채팅방 생성
        ChatRoomModel room = new ChatRoomModel();
        room.setName(roomName);
        room.setCreatedBy(userId);
        LocalDateTime now = LocalDateTime.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);
        chatRoomMapper.insertChatRoom(room);

        // 5. 참여자 추가
        joinChatRoom(userId, room.getId());
        joinChatRoom(friendId, room.getId());

        return room;
    }

    public Long startPrivateChat(Long userId, Long friendId) {
        ChatRoomModel room = createPrivateChatRoom(userId, friendId);
        return room.getId();
    }


    public ChatRoomModel startPrivateChatWithAuthentication(Long friendId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel me = userMapper.selectUserByUsername(username);
        if (me == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        return createPrivateChatRoom(me.getId(), friendId);
    }

    public void joinChatRoomWithAuthentication(Long roomId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel me = userMapper.selectUserByUsername(username);
        if (me == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        joinChatRoom(me.getId(), roomId);
    }

    public void leaveChatRoomWithAuthentication(Long roomId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel me = userMapper.selectUserByUsername(username);
        if (me == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        leaveChatRoom(me.getId(), roomId);
    }

    public List<ChatRoomModel> getMyRoomsWithAuthentication() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel me = userMapper.selectUserByUsername(username);
        if (me == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        return getChatRoomsByUserId(me.getId());
    }


    public void handleWebSocketMessage(ChatMessageModel message, String username, SimpMessagingTemplate messagingTemplate) {
        // 사용자 정보 조회
        UserModel sender = userMapper.selectUserByUsername(username);
        if (sender == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        Long chatRoomId = message.getChatRoomId();
        ChatRoomModel room = chatRoomMapper.selectChatRoomById(chatRoomId);
        if (room == null || room.getDeletedAt() != null) {
            throw new CustomException(CustomExceptionEnum.CHAT_ROOM_BLOCKED);
        }

        // 채팅방이 1:1이라면 수신자 찾아서 차단 관계 확인
        if (room.getName().contains(" - ")) {
            List<UserChatRoomModel> participants = userChatRoomMapper.selectUsersByRoomId(chatRoomId);
            Long receiverId = participants.stream()
                    .map(UserChatRoomModel::getUserId)
                    .filter(id -> !id.equals(sender.getId()))
                    .findFirst()
                    .orElse(null);

            if (receiverId != null) {
                // 차단 관계 확인 (양방향)
                FriendshipModel blocked = friendshipMapper.selectBlockedRelation(sender.getId(), receiverId);
                if (blocked != null) {
                    throw new CustomException(CustomExceptionEnum.BLOCKED_USER);
                }
            }
        }

        // 메시지 저장
        ChatMessageModel saved = saveMessage(message.getChatRoomId(), sender.getId(), message.getMessage());
        saved.setNickname(sender.getNickname());

        // 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/room." + message.getChatRoomId(),
                saved
        );
    }
}
