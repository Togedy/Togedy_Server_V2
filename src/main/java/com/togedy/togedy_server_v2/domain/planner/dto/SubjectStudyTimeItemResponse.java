package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectStudyTimeItemResponse {

    private Long subjectId;
    private String subjectName;
    private String studyColor;
    private Long studyTime;

    public static SubjectStudyTimeItemResponse of(Long subjectId, String subjectName, String studyColor, Long studyTime) {
        return SubjectStudyTimeItemResponse.builder()
                .subjectId(subjectId)
                .subjectName(subjectName)
                .studyColor(studyColor)
                .studyTime(studyTime)
                .build();
    }
}
