package com.togedy.togedy_server_v2.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchUserOnboardingRequest {

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Size(min = 2, max = 10, message = "2자 이상 10자 이하")
    private String nickname;

    @NotNull(message = "생년월일은 필수 항목입니다.")
    @Past(message = "생년월일은 오늘 이전 날짜여야 합니다.")
    private LocalDate birthDate;
}
