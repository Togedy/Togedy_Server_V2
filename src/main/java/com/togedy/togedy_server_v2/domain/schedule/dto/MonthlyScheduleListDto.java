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
public class MonthlyScheduleListDto implements ScheduleComparable {

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String scheduleName;

    private ScheduleType scheduleType;

    private String universityAdmissionStage;

    private String universityAdmissionType;

    private String universityAdmissionMethod;

    private CategoryDto category;

    public static MonthlyScheduleListDto from(UniversityAdmissionSchedule universityAdmissionSchedule) {
        return MonthlyScheduleListDto.builder()
                .startDate(universityAdmissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(universityAdmissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(universityAdmissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(universityAdmissionSchedule.getUniversitySchedule().getEndTime())
                .scheduleName(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getName())
                .scheduleType(ScheduleType.UNIVERSITY)
                .universityAdmissionStage(universityAdmissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getAdmissionType())
                .universityAdmissionMethod(universityAdmissionSchedule.getUniversityAdmissionMethod().getName())
                .category(CategoryDto.temp())
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
                .category(CategoryDto.from(userSchedule.getCategory()))
                .build();
    }
}
