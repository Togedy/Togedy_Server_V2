package com.togedy.togedy_server_v2.domain.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectStudyTimeItemResponse {

    private Long subjectId;
    private String subjectName;
    private Long studyTime;

    public static SubjectStudyTimeItemResponse of(Long subjectId, String subjectName, Long studyTime) {
        return SubjectStudyTimeItemResponse.builder()
                .subjectId(subjectId)
                .subjectName(subjectName)
                .studyTime(studyTime)
                .build();
    }
}
