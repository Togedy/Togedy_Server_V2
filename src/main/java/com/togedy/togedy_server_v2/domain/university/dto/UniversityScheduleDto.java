package com.togedy.togedy_server_v2.domain.university.dto;

import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class UniversityScheduleDto {

    private Long universityScheduleId;
    private String universityAdmissionStage;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;

    public static UniversityScheduleDto from(UniversitySchedule universitySchedule) {
        return UniversityScheduleDto.builder()
                .universityScheduleId(universitySchedule.getId())
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .startDate(universitySchedule.getStartDate())
                .startTime(universitySchedule.getStartTime())
                .endDate(universitySchedule.getEndDate())
                .endTime(universitySchedule.getEndTime())
                .build();
    }

}
