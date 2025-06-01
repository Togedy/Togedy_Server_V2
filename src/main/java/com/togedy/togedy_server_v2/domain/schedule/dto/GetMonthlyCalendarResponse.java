package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetMonthlyCalendarResponse {

    List<MonthlyScheduleListDto> monthlyScheduleList;

    public static GetMonthlyCalendarResponse from(List<MonthlyScheduleListDto> scheduleList) {
        return GetMonthlyCalendarResponse.builder()
                .monthlyScheduleList(scheduleList)
                .build();
    }
}
