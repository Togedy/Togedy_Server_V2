package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleType;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class DailyScheduleListDto implements ScheduleComparable {

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

    public static DailyScheduleListDto from(UniversityAdmissionSchedule universityAdmissionSchedule) {
        return DailyScheduleListDto.builder()
                .scheduleId(universityAdmissionSchedule.getUniversitySchedule().getId())
                .scheduleType(ScheduleType.UNIVERSITY)
                .scheduleName(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getName())
                .universityAdmissionStage(universityAdmissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getAdmissionType())
                .startDate(universityAdmissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(universityAdmissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(universityAdmissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(universityAdmissionSchedule.getUniversitySchedule().getEndTime())
                .category(CategoryDto.temp())
                .build();
    }
}
