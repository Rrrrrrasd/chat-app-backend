package com.example.backend.common.mapper;

import com.example.backend.common.model.FriendshipModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendshipMapper {
    // 친구 요청 삽입
    void insertFriendship(FriendshipModel friendship);

    // 친구 관계 단건 조회 (요청자와 수신자 기준)
    FriendshipModel selectFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 특정 유저가 보낸 모든 관계 (상태 상관없이)
    List<FriendshipModel> selectFriendshipsByUserId(@Param("userId") Long userId);

    // 친구 상태인 관계 (ACCEPTED)
    List<FriendshipModel> selectAcceptedFriendships(@Param("userId") Long userId);

    // 내가 받은 친구 요청 목록 (PENDING)
    List<FriendshipModel> selectPendingRequests(@Param("userId") Long userId);

    // 내가 차단한 사용자 목록 (BLOCKED)
    List<FriendshipModel> selectBlockedFriendships(@Param("userId") Long userId);

    void deleteFriendship(@Param("id") Long id);


    FriendshipModel selectBlockedRelation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    // 상태 업데이트
    int updateFriendship(FriendshipModel friendship);
}
