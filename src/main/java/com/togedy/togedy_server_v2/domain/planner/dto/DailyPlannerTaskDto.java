package com.togedy.togedy_server_v2.domain.planner.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTaskDto {

    @Schema(description = "스터디 과목 ID", type = "integer", format = "int64", example = "1")
    private Long subjectId;

    @Schema(description = "스터디 과목 이름", type = "string", example = "수학")
    private String subjectName;

    @Schema(description = "스터디 과목 색상", type = "string", example = "1")
    private String subjectColor;

    @Schema(description = "해당 과목의 일일 누적 공부 시간(초)", type = "integer", format = "int64", example = "7240")
    private Long subjectStudyTime;

    @Schema(description = "해당 과목의 태스크 목록")
    private List<DailyPlannerTaskItemDto> taskList;

    public static DailyPlannerTaskDto of(
            StudySubject studySubject,
            Long subjectStudyTime,
            List<DailyPlannerTaskItemDto> taskList
    ) {
        return DailyPlannerTaskDto.builder()
                .subjectId(studySubject.getId())
                .subjectName(studySubject.getName())
                .subjectColor(studySubject.getColor())
                .subjectStudyTime(subjectStudyTime)
                .taskList(taskList)
                .build();
    }
}
