package com.togedy.togedy_server_v2.domain.planner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyPlannerTaskResponse {

    @Schema(description = "과목별 일간 플래너 목록")
    private List<DailyPlannerTaskDto> dailyPlanner;

    public static GetDailyPlannerTaskResponse of(List<DailyPlannerTaskDto> dailyPlanner) {
        return GetDailyPlannerTaskResponse.builder()
                .dailyPlanner(dailyPlanner)
                .build();
    }
}
