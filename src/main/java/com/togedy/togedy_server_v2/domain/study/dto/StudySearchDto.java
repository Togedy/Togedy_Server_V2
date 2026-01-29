package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudySearchDto {

    private Long studyId;

    private StudyType studyType;

    private String studyName;

    private String studyDescription;

    private String studyTag;

    private String studyTier;

    private String studyLeaderImageUrl;

    private Integer studyMemberCount;

    private Integer studyMemberLimit;

    private String studyImageUrl;

    private Boolean isNewlyCreated;

    private String lastActivatedAt;

    private String challengeGoalTime;

    private Boolean hasPassword;

    public static StudySearchDto of(
            Study study,
            String studyLeaderImageUrl,
            boolean isNewlyCreated,
            String lastActivatedAt,
            String challengeGoalTime
    ) {
        return StudySearchDto.builder()
                .studyId(study.getId())
                .studyType(study.getType())
                .studyName(study.getName())
                .studyDescription(study.getDescription())
                .studyTag(study.getTag().getDescription())
                .studyTier(study.getTier())
                .studyLeaderImageUrl(studyLeaderImageUrl)
                .studyMemberCount(study.getMemberCount())
                .studyMemberLimit(study.getMemberLimit())
                .studyImageUrl(study.getImageUrl())
                .isNewlyCreated(isNewlyCreated)
                .lastActivatedAt(lastActivatedAt)
                .challengeGoalTime(challengeGoalTime)
                .hasPassword(study.hasPassword())
                .build();
    }
}
