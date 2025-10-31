package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyDto {

    private Long studyId;

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
                .studyId(study.getId())
                .studyType(study.getType())
                .challengeGoalTime(TimeUtil.toTimeFormat(study.getGoalTime()))
                .challengeAchievement(challengeAchievement)
                .studyName(study.getName())
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
                .studyId(study.getId())
                .studyType(study.getType())
                .studyName(study.getName())
                .studyMemberCount(study.getMemberCount())
                .activeMemberList(activeMemberList)
                .build();
    }
}
