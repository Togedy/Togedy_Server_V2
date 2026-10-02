package com.togedy.togedy_server_v2.domain.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalendarAnnouncementRequest {

    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(max = 255, message = "내용은 255자 이하여야 합니다.")
    private String content;
}
