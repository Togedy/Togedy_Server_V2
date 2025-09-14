package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyInvitationResponse {

    private Boolean hasPassword;

    private Long studyId;

    public static PostStudyInvitationResponse from(boolean hasPassword, Long studyId) {
        return PostStudyInvitationResponse.builder()
                .hasPassword(hasPassword)
                .studyId(studyId)
                .build();
    }

}
