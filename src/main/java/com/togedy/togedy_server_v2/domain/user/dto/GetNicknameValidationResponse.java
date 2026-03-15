package com.togedy.togedy_server_v2.domain.user.dto;

import com.togedy.togedy_server_v2.domain.user.enums.NicknameValidationReason;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNicknameValidationResponse {

    private boolean available;
    private NicknameValidationReason reason;
    private String message;

    public static GetNicknameValidationResponse of(
            boolean available,
            NicknameValidationReason reason,
            String message
    ) {
        return GetNicknameValidationResponse.builder()
                .available(available)
                .reason(reason)
                .message(message)
                .build();
    }
}
