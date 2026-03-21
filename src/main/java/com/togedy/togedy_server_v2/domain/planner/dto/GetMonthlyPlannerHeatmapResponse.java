package com.togedy.togedy_server_v2.domain.planner.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMonthlyPlannerHeatmapResponse {

    private List<Integer> heatmapList;

    public static GetMonthlyPlannerHeatmapResponse of(List<Integer> heatmapList) {
        return GetMonthlyPlannerHeatmapResponse.builder()
                .heatmapList(heatmapList)
                .build();
    }
}
