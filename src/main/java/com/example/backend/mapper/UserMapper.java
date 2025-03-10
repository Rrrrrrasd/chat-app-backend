package com.example.backend.mapper;

import com.example.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectUserByUsername(@Param("username") String username);
    int insertUser(User user);
}