package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyResponse {

    private Boolean isJoined;
    private Boolean isStudyLeader;
    private String studyName;
    private String studyLeaderName;
    private String studyDescription;
    private String studyImageUrl;
    private String studyTag;
    private String studyTier;
    private Integer studyMemberCount;
    private Integer completedMemberCount;
    private Integer studyMemberLimit;
    private String studyPassword;

    public static GetStudyResponse of(
            boolean isJoined,
            boolean isStudyLeader,
            Study study,
            User leader,
            Integer completedMemberCount,
            String studyPassword
    ) {
        return GetStudyResponse.builder()
                .isJoined(isJoined)
                .isStudyLeader(isStudyLeader)
                .studyName(study.getName())
                .studyLeaderName(leader.getNickname())
                .studyDescription(study.getDescription())
                .studyImageUrl(study.getImageUrl())
                .studyTag(study.getTag().getDescription())
                .studyTier(study.getTier())
                .studyMemberCount(study.getMemberCount())
                .completedMemberCount(completedMemberCount)
                .studyMemberLimit(study.getMemberLimit())
                .studyPassword(studyPassword)
                .build();
    }
}
