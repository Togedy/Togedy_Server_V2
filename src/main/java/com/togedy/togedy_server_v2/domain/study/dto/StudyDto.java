package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.global.util.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyDto {

    private StudyType studyType;

    private String challengeGoalTime;

    private Integer challengeAchievement;

    private String studyName;

    private Integer completedMemberCount;

    private Integer studyMemberCount;

    private List<ActiveMemberDto> activeMemberList;

    public static StudyDto of(
            Study study,
            int challengeAchievement,
            int completedMemberCount,
            List<ActiveMemberDto> activeMemberList
    )
    {
        return StudyDto.builder()
                .studyType(study.getType())
                .challengeGoalTime(DateTimeUtils.timeConvert(study.getGoalTime()))
                .challengeAchievement(challengeAchievement)
                .completedMemberCount(completedMemberCount)
                .studyMemberCount(study.getMemberCount())
                .activeMemberList(activeMemberList)
                .build();
    }

    public static StudyDto of(
            Study study,
            List<ActiveMemberDto> activeMemberList
    )
    {
        return StudyDto.builder()
                .studyType(study.getType())
                .studyMemberCount(study.getMemberCount())
                .activeMemberList(activeMemberList)
                .build();
    }
}
