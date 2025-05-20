package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDdayScheduleResponse {

    private boolean hasDday;
    private String userScheduleName;
    private int remainingDays;

    public static GetDdayScheduleResponse of(UserSchedule userSchedule, int remainingDays) {
        return GetDdayScheduleResponse.builder()
                .hasDday(true)
                .userScheduleName(userSchedule.getName())
                .remainingDays(remainingDays)
                .build();
    }

    public static GetDdayScheduleResponse temp() {
        return GetDdayScheduleResponse.builder()
                .hasDday(false)
                .build();
    }
}
