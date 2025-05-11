package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleType;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleListDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String scheduleName;

    private ScheduleType scheduleType;

    private String universityAdmissionStage;

    private String universityAdmissionType;

    private String categoryColor;

    public static ScheduleListDto from(UniversitySchedule universitySchedule) {
        return ScheduleListDto.builder()
                .startDate(universitySchedule.getStartDate())
                .endDate(universitySchedule.getEndDate())
                .scheduleName(universitySchedule.getAdmissionMethod().getUniversity().getName())
                .scheduleType(ScheduleType.UNIVERSITY)
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .universityAdmissionType(universitySchedule.getAdmissionMethod().getUniversity().getAdmissionType())
                .build();
    }

    public static ScheduleListDto from(UserSchedule userSchedule) {
        return ScheduleListDto.builder()
                .startDate(userSchedule.getStartDate())
                .endDate(userSchedule.getEndDate())
                .scheduleName(userSchedule.getName())
                .scheduleType(ScheduleType.USER)
                .categoryColor(userSchedule.getCategory().getColor())
                .build();
    }
}
