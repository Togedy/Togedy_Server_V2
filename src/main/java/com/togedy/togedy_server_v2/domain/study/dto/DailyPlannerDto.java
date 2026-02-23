package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyPlannerDto {

    private String studySubjectName;

    private List<StudyTaskDto> taskList;

    public static DailyPlannerDto of(StudySubject studySubject, List<StudyTaskDto> taskList) {
        return DailyPlannerDto.builder()
                .studySubjectName(studySubject.getName())
                .taskList(taskList)
                .build();
    }
}
