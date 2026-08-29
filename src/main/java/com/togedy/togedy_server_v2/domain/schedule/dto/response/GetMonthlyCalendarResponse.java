package com.togedy.togedy_server_v2.domain.schedule.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMonthlyCalendarResponse {

    List<MonthlyScheduleInfo> monthlyScheduleList;

    public static GetMonthlyCalendarResponse from(List<MonthlyScheduleInfo> scheduleList) {
        return GetMonthlyCalendarResponse.builder()
                .monthlyScheduleList(scheduleList)
                .build();
    }
}
