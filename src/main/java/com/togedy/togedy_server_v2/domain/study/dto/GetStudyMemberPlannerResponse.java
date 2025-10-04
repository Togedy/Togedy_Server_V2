package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyMemberPlannerResponse {

    private Boolean isMyPlanner;

    private Boolean isPlannerVisible;

    private Integer completedCount;

    private Integer totalPlanCount;

    private List<DailyPlannerDto> dailyPlanner;

    public static GetStudyMemberPlannerResponse of(
            boolean isMyPlanner,
            boolean isPlannerVisible,
            int completedCount,
            int totalPlanCount,
            List<DailyPlannerDto> dailyPlanner
    )
    {
        return GetStudyMemberPlannerResponse.builder()
                .isMyPlanner(isMyPlanner)
                .isPlannerVisible(isPlannerVisible)
                .completedCount(completedCount)
                .totalPlanCount(totalPlanCount)
                .dailyPlanner(dailyPlanner)
                .build();
    }

    public static GetStudyMemberPlannerResponse of(boolean isMyPlanner, boolean isPlannerVisible) {
        return GetStudyMemberPlannerResponse.builder()
                .isMyPlanner(isMyPlanner)
                .isPlannerVisible(isPlannerVisible)
                .build();
    }
}
