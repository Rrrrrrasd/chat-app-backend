package com.example.backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "아이디는 필수 항목입니다.")
    //@Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    //@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
}
