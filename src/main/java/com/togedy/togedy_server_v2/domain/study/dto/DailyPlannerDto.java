package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyPlannerDto {

    private String studyCategoryName;

    private List<PlanDto> planList;

    public static DailyPlannerDto of(StudyCategory studyCategory, List<PlanDto> planList) {
        return DailyPlannerDto.builder()
                .studyCategoryName(studyCategory.getName())
                .planList(planList)
                .build();
    }
}
