package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerShareTaskItemResponse {

    private String taskName;

    @Getter(onMethod_ = @JsonProperty("isChecked"))
    private boolean checked;

    public static DailyPlannerShareTaskItemResponse of(String taskName, boolean checked) {
        return DailyPlannerShareTaskItemResponse.builder()
                .taskName(taskName)
                .checked(checked)
                .build();
    }
}
