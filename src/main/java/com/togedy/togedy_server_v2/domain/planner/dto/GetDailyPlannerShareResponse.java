package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyPlannerShareResponse {

    private LocalDate date;
    private boolean hasDday;
    private String userScheduleName;
    private Integer remainingDays;
    private String totalStudyTime;
    private String plannerImage;
    private List<DailyPlannerShareItemResponse> plannerItemList;
    private List<DailyTimetableItemResponse> timeTableList;

    public static GetDailyPlannerShareResponse of(
            LocalDate date,
            boolean hasDday,
            String userScheduleName,
            Integer remainingDays,
            String totalStudyTime,
            String plannerImage,
            List<DailyPlannerShareItemResponse> plannerItemList,
            List<DailyTimetableItemResponse> timeTableList
    ) {
        return GetDailyPlannerShareResponse.builder()
                .date(date)
                .hasDday(hasDday)
                .userScheduleName(userScheduleName)
                .remainingDays(remainingDays)
                .totalStudyTime(totalStudyTime)
                .plannerImage(plannerImage)
                .plannerItemList(plannerItemList)
                .timeTableList(timeTableList)
                .build();
    }
}
