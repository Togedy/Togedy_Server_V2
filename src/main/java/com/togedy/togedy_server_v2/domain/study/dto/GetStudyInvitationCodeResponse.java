package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyInvitationCodeResponse {

    private String invitationCode;

    public static GetStudyInvitationCodeResponse from(String invitationCode) {
        return GetStudyInvitationCodeResponse.builder()
                .invitationCode(invitationCode)
                .build();
    }
}
