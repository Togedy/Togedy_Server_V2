package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleType;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.AdmissionSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class MonthlyScheduleListDto {

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalDate endDate;

    private LocalTime endTime;

    private String scheduleName;

    private ScheduleType scheduleType;

    private String universityAdmissionStage;

    private String universityAdmissionType;

    private String categoryColor;

    public static MonthlyScheduleListDto from(AdmissionSchedule admissionSchedule) {
        return MonthlyScheduleListDto.builder()
                .startDate(admissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(admissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(admissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(admissionSchedule.getUniversitySchedule().getEndTime())
                .scheduleName(admissionSchedule.getAdmissionMethod().getUniversity().getName())
                .scheduleType(ScheduleType.UNIVERSITY)
                .universityAdmissionStage(admissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(admissionSchedule.getAdmissionMethod().getUniversity().getAdmissionType())
                .build();
    }

    public static MonthlyScheduleListDto from(UserSchedule userSchedule) {
        return MonthlyScheduleListDto.builder()
                .startDate(userSchedule.getStartDate())
                .startTime(userSchedule.getStartTime())
                .endDate(userSchedule.getEndDate())
                .endTime(userSchedule.getEndTime())
                .scheduleName(userSchedule.getName())
                .scheduleType(ScheduleType.USER)
                .categoryColor(userSchedule.getCategory().getColor())
                .build();
    }
}
