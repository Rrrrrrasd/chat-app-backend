package com.example.backend.common.mapper;

import com.example.backend.common.model.UserProfilesModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    void insertUserProfile(UserProfilesModel userProfile);
    UserProfilesModel selectUserProfileByUserId(@Param("userId") Long userId);
    int updateUserProfile(UserProfilesModel userProfile);
}
