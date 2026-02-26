package com.togedy.togedy_server_v2.domain.planner.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTaskDto {

    private Long subjectId;
    private String subjectName;
    private String subjectColor;
    private String subjectStudyTime;
    private List<DailyPlannerTaskItemDto> taskList;

    public static DailyPlannerTaskDto of(
            StudySubject studySubject,
            String subjectStudyTime,
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
