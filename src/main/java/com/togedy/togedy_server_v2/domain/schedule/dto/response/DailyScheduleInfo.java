package com.togedy.togedy_server_v2.domain.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleComparable;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.schedule.entity.enums.ScheduleType;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionStage;
import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyScheduleInfo implements ScheduleComparable {

    private Long scheduleId;

    private ScheduleType scheduleType;

    private String scheduleName;

    private AdmissionStage universityAdmissionStage;

    private AdmissionType universityAdmissionType;

    private String universityAdmissionMethod;

    private LocalDate startDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    private LocalDate endDate;

    @Schema(type = "string", format = "time", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private CategoryInfo category;

    public static DailyScheduleInfo from(UserSchedule userSchedule) {
        return DailyScheduleInfo.builder()
                .scheduleId(userSchedule.getId())
                .scheduleType(ScheduleType.USER)
                .scheduleName(userSchedule.getName())
                .startDate(userSchedule.getStartDate())
                .startTime(userSchedule.getStartTime())
                .endDate(userSchedule.getEndDate())
                .endTime(userSchedule.getEndTime())
                .category(CategoryInfo.from(userSchedule.getCategory()))
                .build();
    }

    public static DailyScheduleInfo from(UniversityAdmissionSchedule universityAdmissionSchedule) {
        return DailyScheduleInfo.builder()
                .scheduleId(universityAdmissionSchedule.getUniversitySchedule().getId())
                .scheduleType(ScheduleType.UNIVERSITY)
                .scheduleName(universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getName())
                .universityAdmissionStage(universityAdmissionSchedule.getUniversitySchedule().getAdmissionStage())
                .universityAdmissionType(
                        universityAdmissionSchedule.getUniversityAdmissionMethod().getUniversity().getAdmissionType())
                .universityAdmissionMethod(universityAdmissionSchedule.getUniversityAdmissionMethod().getName())
                .startDate(universityAdmissionSchedule.getUniversitySchedule().getStartDate())
                .startTime(universityAdmissionSchedule.getUniversitySchedule().getStartTime())
                .endDate(universityAdmissionSchedule.getUniversitySchedule().getEndDate())
                .endTime(universityAdmissionSchedule.getUniversitySchedule().getEndTime())
                .category(CategoryInfo.temp())
                .build();
    }
}
