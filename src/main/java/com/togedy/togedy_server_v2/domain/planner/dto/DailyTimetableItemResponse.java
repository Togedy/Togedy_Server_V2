package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalTime;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyTimetableItemResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String subjectColor;

    public static DailyTimetableItemResponse of(StudyTime studyTime, String subjectColor) {
        return DailyTimetableItemResponse.builder()
                .startTime(studyTime.getStartTime().toLocalTime())
                .endTime(studyTime.getEndTime().toLocalTime())
                .subjectColor(subjectColor)
                .build();
    }

    public static DailyTimetableItemResponse of(LocalDateTime startTime, LocalDateTime endTime, String subjectColor) {
        return DailyTimetableItemResponse.builder()
                .startTime(startTime.toLocalTime())
                .endTime(endTime.toLocalTime())
                .subjectColor(subjectColor)
                .build();
    }
}
