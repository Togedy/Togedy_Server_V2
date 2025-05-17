package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class GetUserScheduleResponse {

    private String userScheduleName;

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Long categoryId;

    private String memo;

    private Boolean dDay;

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
