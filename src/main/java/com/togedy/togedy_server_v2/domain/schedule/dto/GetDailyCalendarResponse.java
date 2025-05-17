package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetDailyCalendarResponse {

    List<DailyScheduleListDto> scheduleList;

    public static GetDailyCalendarResponse from(List<DailyScheduleListDto> scheduleList) {
        return GetDailyCalendarResponse.builder()
                .scheduleList(scheduleList)
                .build();
    }
}

