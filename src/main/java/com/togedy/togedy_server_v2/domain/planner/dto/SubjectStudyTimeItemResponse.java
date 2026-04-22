package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectStudyTimeItemResponse {

    private Long subjectId;
    private String subjectName;
    private String subjectColor;
    private Long studyTime;

    public static SubjectStudyTimeItemResponse of(Long subjectId, String subjectName, String subjectColor, Long studyTime) {
        return SubjectStudyTimeItemResponse.builder()
                .subjectId(subjectId)
                .subjectName(subjectName)
                .subjectColor(subjectColor)
                .studyTime(studyTime)
                .build();
    }
}
