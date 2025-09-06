package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyResponse {

    private String studyName;
    private String studyLeaderName;
    private String studyDescription;
    private String studyImageUrl;
    private String studyTier;
    private Integer studyMemberCount;
    private Integer studyMemberLimit;

    public static GetStudyResponse of(Study study, User leader) {
        return GetStudyResponse.builder()
                .studyName(study.getName())
                .studyLeaderName(leader.getNickname())
                .studyDescription(study.getDescription())
                .studyImageUrl(study.getImageUrl())
                .studyTier(study.getTier())
                .studyMemberCount(study.getMemberCount())
                .studyMemberLimit(study.getMemberLimit())
                .build();
    }

}
