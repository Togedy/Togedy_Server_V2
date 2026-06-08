package com.togedy.togedy_server_v2.domain.schedule.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyCalendarResponse {

    private Integer remainingDays;
    private List<DailyScheduleListDto> dailyScheduleList;

    public static GetDailyCalendarResponse from(Integer remainingDays, List<DailyScheduleListDto> scheduleList) {
        return GetDailyCalendarResponse.builder()
                .remainingDays(remainingDays)
                .dailyScheduleList(scheduleList)
                .build();
    }
}

