package com.togedy.togedy_server_v2.domain.planner.dto;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudySubjectResponse {

    private Long subjectId;
    private String subjectName;
    private String subjectColor;
    private Long orderIndex;

    public static GetStudySubjectResponse from(StudySubject studySubject) {
        return GetStudySubjectResponse.builder()
                .subjectId(studySubject.getId())
                .subjectName(studySubject.getName())
                .subjectColor(studySubject.getColor())
                .orderIndex(studySubject.getOrderIndex())
                .build();
    }
}
