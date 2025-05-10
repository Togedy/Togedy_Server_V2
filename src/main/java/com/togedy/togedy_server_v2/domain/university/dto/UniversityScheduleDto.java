package com.togedy.togedy_server_v2.domain.university.dto;

import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UniversityScheduleDto {

    private Long universityScheduleId;

    private String universityAdmissionStage;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public static UniversityScheduleDto from(UniversitySchedule universitySchedule) {
        return UniversityScheduleDto.builder()
                .universityScheduleId(universitySchedule.getId())
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .startDate(universitySchedule.getStartDate())
                .endDate(universitySchedule.getEndDate())
                .build();
    }

}
