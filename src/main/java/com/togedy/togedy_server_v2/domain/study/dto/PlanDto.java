package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.Plan;
import com.togedy.togedy_server_v2.domain.planner.enums.PlanStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanDto {

    private String planName;

    private PlanStatus planStatus;

    public static PlanDto from(Plan plan) {
        return PlanDto.builder()
                .planName(plan.getName())
                .planStatus(plan.getStatus())
                .build();
    }
}
