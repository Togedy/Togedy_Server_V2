package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyResponse {

    private Boolean isStudyLeader;
    private String studyName;
    private String studyLeaderName;
    private String studyDescription;
    private String studyImageUrl;
    private String studyTag;
    private String studyTier;
    private Integer studyMemberCount;
    private Integer studyMemberLimit;
    private String studyPassword;

    public static GetStudyResponse of(Study study, User leader, String studyPassword) {
        return GetStudyResponse.builder()
                .isStudyLeader(true)
                .studyName(study.getName())
                .studyLeaderName(leader.getNickname())
                .studyDescription(study.getDescription())
                .studyImageUrl(study.getImageUrl())
                .studyTag(study.getTag())
                .studyTier(study.getTier())
                .studyMemberCount(study.getMemberCount())
                .studyMemberLimit(study.getMemberLimit())
                .studyPassword(studyPassword)
                .build();
    }

    public static GetStudyResponse of(Study study, User leader) {
        return GetStudyResponse.builder()
                .isStudyLeader(false)
                .studyName(study.getName())
                .studyLeaderName(leader.getNickname())
                .studyDescription(study.getDescription())
                .studyImageUrl(study.getImageUrl())
                .studyTag(study.getTag())
                .studyTier(study.getTier())
                .studyMemberCount(study.getMemberCount())
                .studyMemberLimit(study.getMemberLimit())
                .build();
    }
}
