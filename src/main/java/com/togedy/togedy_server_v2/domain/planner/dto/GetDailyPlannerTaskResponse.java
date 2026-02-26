package com.togedy.togedy_server_v2.domain.planner.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyPlannerTaskResponse {

    private List<DailyPlannerTaskDto> dailyPlanner;

    public static GetDailyPlannerTaskResponse of(List<DailyPlannerTaskDto> dailyPlanner) {
        return GetDailyPlannerTaskResponse.builder()
                .dailyPlanner(dailyPlanner)
                .build();
    }
}
