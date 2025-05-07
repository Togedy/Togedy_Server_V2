package com.togedy.togedy_server_v2.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginUserRequest {

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email
    private String email;
}
