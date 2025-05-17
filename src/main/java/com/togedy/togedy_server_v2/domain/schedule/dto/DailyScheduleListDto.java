package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleType;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.AdmissionSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class DailyScheduleListDto {

    private Long scheduleId;

    private ScheduleType scheduleType;

    private String scheduleName;

    private String universityAdmissionStage;

    private String universityAdmissionType;

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private CategoryDto category;

    public static DailyScheduleListDto from(UserSchedule userSchedule) {
        return DailyScheduleListDto.builder()
                .scheduleId(userSchedule.getId())
                .scheduleType(ScheduleType.USER)
                .scheduleName(userSchedule.getName())
                .startDate(userSchedule.getStartDate())
                .startTime(userSchedule.getStartTime())
                .endDate(userSchedule.getEndDate())
                .endTime(userSchedule.getEndTime())
                .category(CategoryDto.from(userSchedule.getCategory()))
                .build();
    }

    public static DailyScheduleListDto from(AdmissionSchedule admissionSchedule) {
        return DailyScheduleListDto.builder()
                .scheduleId(admissionSchedule.getUniversitySchedule().getId())
                .scheduleType(ScheduleType.UNIVERSITY)
                .scheduleName(admissionSchedule.getAdmissionMethod().getUniversity().getName())
                .universityAdmissionStage(admissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(admissionSchedule.getAdmissionMethod().getUniversity().getAdmissionType())
                .startDate(admissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(admissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(admissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(admissionSchedule.getUniversitySchedule().getEndTime())
                .category(CategoryDto.temp())
                .build();
    }
}
