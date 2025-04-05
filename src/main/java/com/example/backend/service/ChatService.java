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
    private final MessageReadMapper messageReadMapper;
    private final NotificationService notificationService;

    // 채팅방 생성 (PUBLIC 채팅방)
    public ChatRoomModel createChatRoom(String name, Long createdBy) {
        ChatRoomModel room = new ChatRoomModel();
        room.setName(name);
        room.setRoomType("PUBLIC");
        room.setCreatedBy(createdBy);
        LocalDateTime now = LocalDateTime.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);

        chatRoomMapper.insertChatRoom(room);
        return room;
    }

    // 1:1 채팅방 생성 (PRIVATE)
    public ChatRoomModel createPrivateChatRoom(Long userId, Long friendId) {
        // 차단 여부 및 친구 상태 체크 (생략: 기존 로직 유지)
        FriendshipModel friendship = friendshipMapper.selectFriendship(userId, friendId);
        if (friendship == null || !"ACCEPTED".equals(friendship.getStatus())) {
            throw new CustomException(CustomExceptionEnum.NOT_FRIEND);
        }

        // 기존 채팅방 존재 여부 체크
        ChatRoomModel existingRoom = chatRoomMapper.selectPrivateRoomByUserIds(userId, friendId);
        if (existingRoom != null) return existingRoom;

        // 사용자 정보 조회 및 채팅방 이름 생성
        UserModel me = userMapper.selectUserById(userId);
        UserModel friend = userMapper.selectUserById(friendId);
        String roomName = me.getNickname() + " - " + friend.getNickname();

        // 채팅방 생성
        ChatRoomModel room = new ChatRoomModel();
        room.setName(roomName);
        room.setRoomType("PRIVATE");
        room.setCreatedBy(userId);
        LocalDateTime now = LocalDateTime.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);
        chatRoomMapper.insertChatRoom(room);

        // 참여자 추가
        joinChatRoom(userId, room.getId());
        joinChatRoom(friendId, room.getId());

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

    // 메시지 읽음 처리 메서드 예시
    public void markMessageAsRead(Long messageId, Long userId) {
        MessageReadModel readModel = messageReadMapper.selectMessageRead(messageId, userId);
        LocalDateTime now = LocalDateTime.now();
        if (readModel == null) {
            readModel = new MessageReadModel();
            readModel.setMessageId(messageId);
            readModel.setUserId(userId);
            readModel.setReadAt(now);
            messageReadMapper.insertMessageRead(readModel);
        } else {
            readModel.setReadAt(now);
            messageReadMapper.updateMessageRead(readModel);
        }
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

        // 현재 인증 사용자를 조회
        UserModel currentUser = userMapper.selectUserByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        for (ChatMessageModel msg : messages) {
            // 메시지 작성자 정보 조회 및 닉네임 설정
            UserModel sender = userMapper.selectUserById(msg.getSenderId());
            msg.setNickname(sender.getNickname());

            // 현재 사용자가 메시지 작성자와 다를 경우에만 차단 여부 확인
            if (!sender.getId().equals(currentUser.getId())) {
                // 현재 사용자가 해당 작성자를 차단한 경우
                FriendshipModel blockRelation = friendshipMapper.selectBlockedRelation(currentUser.getId(), sender.getId());
                if (blockRelation != null && "BLOCKED".equals(blockRelation.getStatus())) {
                    // 메시지 내용 변경
                    msg.setMessage("차단된 채팅입니다");
                }
            }
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
        // 메시지 작성자 조회 및 유효성 검증
        UserModel sender = userMapper.selectUserByUsername(username);
        if (sender == null) {
            throw new CustomException(CustomExceptionEnum.USER_NOT_FOUND);
        }

        Long chatRoomId = message.getChatRoomId();
        ChatRoomModel room = chatRoomMapper.selectChatRoomById(chatRoomId);
        if (room == null || room.getDeletedAt() != null) {
            throw new CustomException(CustomExceptionEnum.CHAT_ROOM_BLOCKED);
        }

        // 원본 메시지를 DB에 저장 (내용은 그대로 저장)
        ChatMessageModel saved = saveMessage(chatRoomId, sender.getId(), message.getMessage());
        saved.setNickname(sender.getNickname());

        // 채팅방에 참여한 모든 사용자 조회
        List<UserChatRoomModel> participants = userChatRoomMapper.selectUsersByRoomId(chatRoomId);
        for (UserChatRoomModel participant : participants) {
            Long recipientId = participant.getUserId();
            // 원본 메시지를 복제하여 개별 전송용 메시지 객체 생성
            ChatMessageModel messageToSend = new ChatMessageModel();
            messageToSend.setId(saved.getId());
            messageToSend.setChatRoomId(saved.getChatRoomId());
            messageToSend.setSenderId(saved.getSenderId());
            messageToSend.setNickname(saved.getNickname());
            messageToSend.setCreatedAt(saved.getCreatedAt());
            messageToSend.setUpdatedAt(saved.getUpdatedAt());

            // 수신자가 작성자와 다를 경우 차단 관계를 확인
            if (!recipientId.equals(sender.getId())) {
                FriendshipModel blockRelation = friendshipMapper.selectBlockedRelation(recipientId, sender.getId());
                if (blockRelation != null && "BLOCKED".equals(blockRelation.getStatus())) {
                    messageToSend.setMessage("차단된 채팅입니다");
                } else {
                    messageToSend.setMessage(saved.getMessage());
                    // 채팅 알림 생성 코드 삭제: 채팅 메시지에 대한 알림을 생성하지 않음
                }
            } else {
                messageToSend.setMessage(saved.getMessage());
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(recipientId), "/queue/messages", messageToSend);
        }

    }
}
