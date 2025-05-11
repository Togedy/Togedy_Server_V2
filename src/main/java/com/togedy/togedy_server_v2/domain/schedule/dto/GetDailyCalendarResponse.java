package com.togedy.togedy_server_v2.domain.schedule.dto;

import com.togedy.togedy_server_v2.domain.schedule.entity.ScheduleType;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetDailyCalendarResponse {

    private Long scheduleId;
    private ScheduleType scheduleType;
    private String scheduleName;
    private String universityAdmissionStage;
    private String universityAdmissionType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CategoryDto category;

    public static GetDailyCalendarResponse from(UserSchedule userSchedule) {
        return GetDailyCalendarResponse.builder()
                .scheduleId(userSchedule.getId())
                .scheduleType(ScheduleType.USER)
                .scheduleName(userSchedule.getName())
                .startDate(userSchedule.getStartDate())
                .endDate(userSchedule.getEndDate())
                .category(CategoryDto.from(userSchedule.getCategory()))
                .build();
    }

    public static GetDailyCalendarResponse from(UniversitySchedule universitySchedule) {
        return GetDailyCalendarResponse.builder()
                .scheduleId(universitySchedule.getId())
                .scheduleType(ScheduleType.UNIVERSITY)
                .scheduleName(universitySchedule.getAdmissionMethod().getUniversity().getName())
                .universityAdmissionStage(universitySchedule.getAdmissionStage())
                .universityAdmissionType(universitySchedule.getAdmissionMethod().getUniversity().getAdmissionType())
                .startDate(universitySchedule.getStartDate())
                .endDate(universitySchedule.getEndDate())
                .build();
    }
}
