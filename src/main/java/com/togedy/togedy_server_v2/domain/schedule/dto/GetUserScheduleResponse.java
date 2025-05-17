package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class GetUserScheduleResponse {
    private String userScheduleName;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private Long categoryId;
    private String memo;
    @JsonProperty("d-day")
    private boolean dDay;

    public static GetUserScheduleResponse from(UserSchedule userSchedule) {
        return GetUserScheduleResponse.builder()
                .userScheduleName(userSchedule.getName())
                .startDate(userSchedule.getStartDate())
                .startTime(userSchedule.getStartTime())
                .endDate(userSchedule.getEndDate())
                .endTime(userSchedule.getEndTime())
                .categoryId(userSchedule.getCategory().getId())
                .memo(userSchedule.getMemo())
                .dDay(userSchedule.isDDay())
                .build();
    }
}
