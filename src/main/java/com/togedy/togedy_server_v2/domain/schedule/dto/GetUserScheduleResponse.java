package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetUserScheduleResponse {
    private String userScheduleName;
    private LocalDateTime startDate;
    private boolean allDayStart;
    private LocalDateTime endDate;
    private boolean allDayEnd;
    private Long categoryId;
    private String memo;
    private boolean dDay;

    public static GetUserScheduleResponse from(UserSchedule userSchedule) {
        return GetUserScheduleResponse.builder()
                .userScheduleName(userSchedule.getName())
                .startDate(userSchedule.getStartDate())
                .allDayStart(userSchedule.isAllDayStart())
                .allDayEnd(userSchedule.isAllDayEnd())
                .categoryId(userSchedule.getCategory().getId())
                .memo(userSchedule.getMemo())
                .dDay(userSchedule.isDDay())
                .build();
    }
}
