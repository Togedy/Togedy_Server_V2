package com.togedy.togedy_server_v2.domain.study.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyTaskDto {

    private String taskName;

    private boolean isChecked;

    public static StudyTaskDto from(StudyTask studyTask) {
        return StudyTaskDto.builder()
                .taskName(studyTask.getName())
                .isChecked(studyTask.isChecked())
                .build();
    }
}
