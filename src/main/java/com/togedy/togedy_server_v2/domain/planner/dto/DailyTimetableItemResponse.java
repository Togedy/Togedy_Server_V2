package com.togedy.togedy_server_v2.domain.planner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalTime;
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
}
