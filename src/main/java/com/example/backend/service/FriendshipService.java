package com.example.backend.service;

import com.example.backend.common.dto.FriendRequestDTO;
import com.example.backend.common.dto.FriendResponseDTO;
import com.example.backend.common.mapper.ChatRoomMapper;
import com.example.backend.common.mapper.FriendshipMapper;
import com.example.backend.common.mapper.UserMapper;
import com.example.backend.common.model.ChatRoomModel;
import com.example.backend.common.model.FriendshipModel;
import com.example.backend.common.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final ChatRoomMapper chatRoomMapper;
    private final NotificationService notificationService;

    // 1. 친구 요청 (PENDING)
    public void addFriend(FriendshipModel friendship) {
        LocalDateTime now = LocalDateTime.now();
        friendship.setCreatedAt(now);
        friendship.setUpdatedAt(now);
        friendship.setStatus("PENDING");
        friendshipMapper.insertFriendship(friendship);
        // 로그 추가
        System.out.println("친구 요청 삽입 완료: " + friendship);


        // 친구 요청을 보낸 후, 대상 사용자에게 알림 생성
        UserModel sender = getCurrentUser(); // 현재 로그인 사용자
        UserModel targetUser = userMapper.selectUserById(friendship.getFriendId());
        if (targetUser != null) {
            String message = sender.getNickname() + "님에게서 친구요청이 왔습니다.";
            notificationService.createNotification(
                    targetUser.getId(),
                    "FRIEND_REQUEST",
                    message
            );
        }
    }

    // 2. 친구 요청 수락/거절 (ACCEPTED / REJECTED)
    public void updateFriendship(FriendshipModel friendship) {
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipMapper.updateFriendship(friendship);
    }

    // 3. 나의 친구 목록 (ACCEPTED 상태)
    public List<FriendshipModel> getAcceptedFriends(Long userId) {
        return friendshipMapper.selectAcceptedFriendships(userId);
    }

    public List<FriendResponseDTO> getAcceptedFriendNicknamesSecure() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserModel me = userMapper.selectUserByUsername(username);
        Long myId = me.getId();

        List<FriendshipModel> rawList = friendshipMapper.selectAcceptedFriendships(myId);

        // 상대방 ID 추출 → 닉네임 붙이기
        return rawList.stream().map(f -> {
            Long friendId = f.getUserId().equals(myId) ? f.getFriendId() : f.getUserId();
            UserModel friendUser = userMapper.selectUserById(friendId);

            return new FriendResponseDTO(f.getId(), friendId, friendUser.getNickname());
        }).toList();
    }

    // 4. 나에게 온 친구 요청 목록 (PENDING 상태)
    public List<FriendshipModel> getPendingRequests(Long userId) {
        System.out.println("💡 받은 친구 요청 조회: userId = " + userId);
        return friendshipMapper.selectPendingRequests(userId);
    }

    // 5. 차단 목록 (BLOCKED 상태)
    public List<FriendshipModel> getBlockedFriends(Long userId) {
        return friendshipMapper.selectFriendshipsByUserId(userId).stream()
                .filter(f -> "BLOCKED".equals(f.getStatus()))
                .toList();
    }

    // 6. 차단
    public void blockFriendSecure(Long targetId) {
        UserModel me = getCurrentUser();

        // 기존 친구 관계 조회
        FriendshipModel existing = friendshipMapper.selectFriendship(me.getId(), targetId);

        if (existing != null && existing.getUserId().equals(me.getId())) {
            existing.setStatus("BLOCKED");
            existing.setUpdatedAt(LocalDateTime.now());
            friendshipMapper.updateFriendship(existing);
        } else {
            if (existing != null) {
                friendshipMapper.deleteFriendship(existing.getId());
            }
            FriendshipModel newBlock = new FriendshipModel();
            newBlock.setUserId(me.getId());
            newBlock.setFriendId(targetId);
            newBlock.setStatus("BLOCKED");
            newBlock.setCreatedAt(LocalDateTime.now());
            newBlock.setUpdatedAt(LocalDateTime.now());
            friendshipMapper.insertFriendship(newBlock);
        }

        // 1:1 채팅방 soft delete 수행
        ChatRoomModel privateRoom = chatRoomMapper.selectPrivateRoomByUserIds(me.getId(), targetId);
        if (privateRoom == null) {
            privateRoom = chatRoomMapper.selectPrivateRoomByUserIds(targetId, me.getId());
        }

        if (privateRoom != null && privateRoom.getDeletedAt() == null) {
            chatRoomMapper.softDeletePrivateChatRoomById(
                    privateRoom.getId(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        }
    }

    // 7. 차단 해제
    public void unblockFriendSecure(Long targetId) {
        UserModel me = getCurrentUser();
        FriendshipModel existing = friendshipMapper.selectFriendship(me.getId(), targetId);

        if (existing != null && "BLOCKED".equals(existing.getStatus())) {
            existing.setStatus("ACCEPTED");
            existing.setUpdatedAt(LocalDateTime.now());
            friendshipMapper.updateFriendship(existing);

            // 차단 해제 시 채팅방 복원
            ChatRoomModel privateRoom = chatRoomMapper.selectPrivateRoomByUserIds(me.getId(), targetId);
            if (privateRoom == null) {
                privateRoom = chatRoomMapper.selectPrivateRoomByUserIds(targetId, me.getId());
            }

            if (privateRoom != null && privateRoom.getDeletedAt() != null) {
                chatRoomMapper.restorePrivateChatRoomById(privateRoom.getId());
            }
        }
    }

    // 8. 현재 로그인 유저
    private UserModel getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.selectUserByUsername(username);
    }

    // 닉네임으로 친구 요청
    public String addFriendByNickname(Long userId, String nickname) {
        UserModel friend = userMapper.selectUserByNickname(nickname);
        if (friend == null) {
            return "존재하지 않는 닉네임입니다.";
        }
        if (friend.getId().equals(userId)) {
            return "자기 자신에게 친구 요청을 보낼 수 없습니다.";
        }
        FriendshipModel existing = friendshipMapper.selectFriendship(userId, friend.getId());
        if (existing != null && !"REJECTED".equals(existing.getStatus())) {
            return "이미 친구 요청을 보냈거나 친구 상태입니다.";
        }
        FriendshipModel friendship = new FriendshipModel();
        friendship.setUserId(userId);
        friendship.setFriendId(friend.getId());
        friendship.setStatus("PENDING");
        friendship.setCreatedAt(LocalDateTime.now());
        friendship.setUpdatedAt(LocalDateTime.now());
        friendshipMapper.insertFriendship(friendship);

        // 친구 요청 알림 생성 추가
        // 예: 현재 요청 보낸 사용자의 닉네임을 사용해서 알림 메시지 생성
        UserModel sender = userMapper.selectUserById(userId);
        String notificationMessage = sender.getNickname() + "님에게서 친구요청이 왔습니다.";
        notificationService.createNotification(friend.getId(), "FRIEND_REQUEST", notificationMessage);

        return "친구 요청을 보냈습니다.";
    }

    public String addFriendByNicknameUsingUsername(String nickname) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserModel me = userMapper.selectUserByUsername(username);
        if (me == null) return "로그인 정보가 유효하지 않습니다.";

        return addFriendByNickname(me.getId(), nickname);
    }

    public List<FriendRequestDTO> getPendingRequestDTOsSecure() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserModel me = userMapper.selectUserByUsername(username);
        Long myId = me.getId();

        List<FriendshipModel> pending = friendshipMapper.selectPendingRequests(myId);

        return pending.stream()
                .map(req -> {
                    UserModel requester = userMapper.selectUserById(req.getUserId());
                    return new FriendRequestDTO(req.getId(), requester.getId(), requester.getNickname());
                })
                .toList();
    }

    public List<FriendResponseDTO> getBlockedFriendNicknamesSecure() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserModel me = userMapper.selectUserByUsername(username);
        Long myId = me.getId();

        // selectBlockedFriendships 메서드가 FriendshipModel 객체 리스트를 반환한다고 가정
        List<FriendshipModel> rawList = friendshipMapper.selectBlockedFriendships(myId);

        return rawList.stream().map(f -> {
            // 차단 관계에서는 현재 사용자가 userId이므로, 차단 대상은 friendId
            UserModel friendUser = userMapper.selectUserById(f.getFriendId());
            return new FriendResponseDTO(f.getId(), f.getFriendId(), friendUser.getNickname());
        }).toList();
    }
}
