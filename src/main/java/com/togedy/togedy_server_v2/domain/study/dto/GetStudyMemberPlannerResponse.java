package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyMemberPlannerResponse {

    private Boolean isMyPlanner;

    private Boolean isPublic;

    private Integer completedCount;

    private Integer totalPlanCount;

    private List<DailyPlannerDto> dailyPlanner;

    public static GetStudyMemberPlannerResponse of(
            boolean isMyPlanner,
            boolean isPublic,
            int completedCount,
            int totalPlanCount,
            List<DailyPlannerDto> dailyPlanner
    )
    {
        return GetStudyMemberPlannerResponse.builder()
                .isMyPlanner(isMyPlanner)
                .isPublic(isPublic)
                .completedCount(completedCount)
                .totalPlanCount(totalPlanCount)
                .dailyPlanner(dailyPlanner)
                .build();
    }

    public static GetStudyMemberPlannerResponse of(boolean isMyPlanner, boolean isPublic) {
        return GetStudyMemberPlannerResponse.builder()
                .isMyPlanner(isMyPlanner)
                .isPublic(isPublic)
                .build();
    }
}
