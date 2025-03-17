package com.example.backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequestDTO{
    @NotBlank(message = "아이디는 필수 항목입니다.")
    //@Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    //@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    //@Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;
}
