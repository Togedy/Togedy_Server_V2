package com.togedy.togedy_server_v2.domain.university.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UniversityScheduleInfo {

    private String universityAdmissionStage;

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    public static UniversityScheduleInfo from(UniversitySchedule universitySchedule) {
        return UniversityScheduleInfo.builder()
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .startDate(universitySchedule.getStartDate())
                .startTime(universitySchedule.getStartTime())
                .endDate(universitySchedule.getEndDate())
                .endTime(universitySchedule.getEndTime())
                .build();
    }

}
