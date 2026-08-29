package com.togedy.togedy_server_v2.domain.schedule.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyCalendarResponse {

    private Integer remainingDays;
    private List<DailyScheduleInfo> dailyScheduleList;

    public static GetDailyCalendarResponse from(Integer remainingDays, List<DailyScheduleInfo> scheduleList) {
        return GetDailyCalendarResponse.builder()
                .remainingDays(remainingDays)
                .dailyScheduleList(scheduleList)
                .build();
    }
}

