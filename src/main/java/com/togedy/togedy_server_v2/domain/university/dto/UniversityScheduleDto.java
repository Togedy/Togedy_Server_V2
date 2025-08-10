package com.togedy.togedy_server_v2.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class UniversityScheduleDto {

    private String universityAdmissionStage;

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    public static UniversityScheduleDto from(UniversitySchedule universitySchedule) {
        return UniversityScheduleDto.builder()
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .startDate(universitySchedule.getStartDate())
                .startTime(universitySchedule.getStartTime())
                .endDate(universitySchedule.getEndDate())
                .endTime(universitySchedule.getEndTime())
                .build();
    }

}
