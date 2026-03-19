package com.togedy.togedy_server_v2.domain.planner.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerShareItemResponse {

    private Long subjectId;
    private String subjectName;
    private String subjectColor;
    private int totalTaskCount;
    private int checkedTaskCount;
    private List<DailyPlannerShareTaskItemResponse> taskList;

    public static DailyPlannerShareItemResponse of(
            Long subjectId,
            String subjectName,
            String subjectColor,
            int totalTaskCount,
            int checkedTaskCount,
            List<DailyPlannerShareTaskItemResponse> taskList
    ) {
        return DailyPlannerShareItemResponse.builder()
                .subjectId(subjectId)
                .subjectName(subjectName)
                .subjectColor(subjectColor)
                .totalTaskCount(totalTaskCount)
                .checkedTaskCount(checkedTaskCount)
                .taskList(taskList)
                .build();
    }
}
