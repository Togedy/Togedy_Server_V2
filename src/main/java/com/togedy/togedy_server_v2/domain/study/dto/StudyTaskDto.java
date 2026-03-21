package com.togedy.togedy_server_v2.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyTaskDto {

    @Schema(description = "스터디 태스크 이름", type = "string", example = "강의")
    private String taskName;

    @Getter(onMethod_ = @JsonProperty("isChecked"))
    @Schema(name = "isChecked", description = "태스크 완료 여부", type = "boolean", example = "true")
    private boolean checked;

    public static StudyTaskDto from(StudyTask studyTask) {
        return StudyTaskDto.builder()
                .taskName(studyTask.getName())
                .checked(studyTask.isChecked())
                .build();
    }
}
