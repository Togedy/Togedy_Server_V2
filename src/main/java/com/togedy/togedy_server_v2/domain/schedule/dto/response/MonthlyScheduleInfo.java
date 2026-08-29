package com.togedy.togedy_server_v2.domain.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.schedule.entity.enums.ScheduleType;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyScheduleInfo implements ScheduleComparable {

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

    private AdmissionType universityAdmissionType;

    private String universityAdmissionMethod;

    private CategoryInfo category;

    public static MonthlyScheduleInfo from(UniversityAdmissionSchedule universityAdmissionSchedule) {
        return MonthlyScheduleInfo.builder()
                .startDate(universityAdmissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(universityAdmissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(universityAdmissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(universityAdmissionSchedule.getUniversitySchedule().getEndTime())
                .scheduleName(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getName())
                .scheduleType(ScheduleType.UNIVERSITY)
                .universityAdmissionStage(universityAdmissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(
                        universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getAdmissionType())
                .universityAdmissionMethod(universityAdmissionSchedule.getUniversityAdmissionMethod().getName())
                .category(CategoryInfo.temp())
                .build();
    }

    public static MonthlyScheduleInfo from(UserSchedule userSchedule) {
        return MonthlyScheduleInfo.builder()
                .startDate(userSchedule.getStartDate())
                .startTime(userSchedule.getStartTime())
                .endDate(userSchedule.getEndDate())
                .endTime(userSchedule.getEndTime())
                .scheduleName(userSchedule.getName())
                .scheduleType(ScheduleType.USER)
                .category(CategoryInfo.from(userSchedule.getCategory()))
                .build();
    }
}
