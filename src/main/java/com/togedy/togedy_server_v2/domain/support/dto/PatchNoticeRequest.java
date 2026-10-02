package com.togedy.togedy_server_v2.domain.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchNoticeRequest {

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    private String noticeTitle;

    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(max = 255, message = "내용은 255자 이하여야 합니다.")
    private String noticeContent;

}
