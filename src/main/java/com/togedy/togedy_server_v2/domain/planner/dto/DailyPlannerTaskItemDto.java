package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTaskItemDto {

    private Long taskId;
    private String taskName;

    @JsonProperty("isChecked")
    private boolean isChecked;

    public static DailyPlannerTaskItemDto from(StudyTask studyTask) {
        return DailyPlannerTaskItemDto.builder()
                .taskId(studyTask.getId())
                .taskName(studyTask.getName())
                .isChecked(studyTask.isChecked())
                .build();
    }
}
