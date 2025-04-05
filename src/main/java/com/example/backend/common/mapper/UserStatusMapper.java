package com.example.backend.common.mapper;

import com.example.backend.common.model.UserStatusModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserStatusMapper {


    void insertUserStatus(UserStatusModel userStatus);


    UserStatusModel selectUserStatusByUserId(@Param("userId") Long userId);


    int updateUserStatus(UserStatusModel userStatus);
}
