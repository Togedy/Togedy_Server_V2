package com.togedy.togedy_server_v2.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetMonthlyCalendarsResponse {

    List<ScheduleListDto> scheduleList;

    public static GetMonthlyCalendarsResponse from(List<ScheduleListDto> scheduleList) {
        return GetMonthlyCalendarsResponse.builder()
                .scheduleList(scheduleList)
                .build();
    }
}
