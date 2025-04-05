package com.example.backend.common.mapper;

import com.example.backend.common.model.MessageReadModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MessageReadMapper {


    void insertMessageRead(MessageReadModel messageRead);


    MessageReadModel selectMessageRead(@Param("messageId") Long messageId, @Param("userId") Long userId);


    int updateMessageRead(MessageReadModel messageRead);
}
