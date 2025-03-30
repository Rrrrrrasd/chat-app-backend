package com.example.backend.common.mapper;


import com.example.backend.common.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserModel selectUserByUsername(@Param("username") String username);
    UserModel selectUserById(@Param("id") Long id);
    int insertUser(UserModel user);
    int updateUser(UserModel user);
}