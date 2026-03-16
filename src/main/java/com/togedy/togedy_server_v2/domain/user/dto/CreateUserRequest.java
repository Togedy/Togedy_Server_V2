package com.togedy.togedy_server_v2.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Size(min = 2, max = 10, message = "2자 이상 10자 이하")
    private String nickname;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email
    private String email;

}
