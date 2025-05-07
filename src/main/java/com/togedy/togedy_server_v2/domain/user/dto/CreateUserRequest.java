package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Size(max = 10, message = "10자 이내")
    private String nickname;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email
    private String email;

    public User toEntity() {
        return User.create(nickname, email);
    }
}
