package com.example.backend.common.mapper;

import com.example.backend.common.model.FriendshipModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendshipMapper {
    void insertFriendship(FriendshipModel friendship);
    FriendshipModel selectFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
    List<FriendshipModel> selectFriendshipsByUserId(@Param("userId") Long userId);
    int updateFriendship(FriendshipModel friendship);
}
