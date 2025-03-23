package com.example.backend.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Data
@Getter
public class ChatRoom {
    private Long id;
    private String name;
    private Long createdBy;
    private LocalDateTime createdAt;
}
