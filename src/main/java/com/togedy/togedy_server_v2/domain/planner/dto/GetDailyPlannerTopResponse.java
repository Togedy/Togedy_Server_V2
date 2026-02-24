package com.togedy.togedy_server_v2.domain.planner.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyPlannerTopResponse {

    private LocalDate date;
    private boolean hasDday;
    private String userScheduleName;
    private Integer remainingDays;
    private String totalStudyTime;
    private String plannerImage;

    public static GetDailyPlannerTopResponse of(
            LocalDate date,
            boolean hasDday,
            String userScheduleName,
            Integer remainingDays,
            String totalStudyTime,
            String plannerImage
    ) {
        return GetDailyPlannerTopResponse.builder()
                .date(date)
                .hasDday(hasDday)
                .userScheduleName(userScheduleName)
                .remainingDays(remainingDays)
                .totalStudyTime(totalStudyTime)
                .plannerImage(plannerImage)
                .build();
    }
}
