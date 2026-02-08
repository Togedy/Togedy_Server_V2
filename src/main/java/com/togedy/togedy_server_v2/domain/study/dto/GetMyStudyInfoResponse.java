package com.togedy.togedy_server_v2.domain.study.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyStudyInfoResponse {

    private boolean hasChallenge;

    private String goalTime;

    private String studyTime;

    private Integer achievement;

    private List<StudyDto> studyList;

    public static GetMyStudyInfoResponse of(
            String goalTime,
            String studyTime,
            int achievement,
            List<StudyDto> studyList
    ) {
        return GetMyStudyInfoResponse.builder()
                .hasChallenge(true)
                .goalTime(goalTime)
                .studyTime(studyTime)
                .achievement(achievement)
                .studyList(studyList)
                .build();
    }

    public static GetMyStudyInfoResponse from(String studyTime, List<StudyDto> studyList) {
        return GetMyStudyInfoResponse.builder()
                .hasChallenge(false)
                .studyTime(studyTime)
                .studyList(studyList)
                .build();
    }
}
