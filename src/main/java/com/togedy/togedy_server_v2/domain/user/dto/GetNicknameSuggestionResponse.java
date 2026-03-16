package com.togedy.togedy_server_v2.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNicknameSuggestionResponse {

    private String nickname;

    public static GetNicknameSuggestionResponse from(String nickname) {
        return GetNicknameSuggestionResponse.builder()
                .nickname(nickname)
                .build();
    }
}
