package com.example.backend.common.mapper;

import com.example.backend.common.model.UserProfileImageModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserProfileImageMapper {


    void insertUserProfileImage(UserProfileImageModel userProfileImage);


    UserProfileImageModel selectUserProfileImageByUserId(@Param("userId") Long userId);


    int updateUserProfileImage(UserProfileImageModel userProfileImage);
}
