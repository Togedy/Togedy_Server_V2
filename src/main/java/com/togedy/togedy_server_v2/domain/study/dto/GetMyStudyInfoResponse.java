package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
    )
    {
        return GetMyStudyInfoResponse.builder()
                .hasChallenge(true)
                .goalTime(goalTime)
                .studyTime(studyTime)
                .achievement(achievement)
                .studyList(studyList)
                .build();
    }

    public static GetMyStudyInfoResponse from(List<StudyDto> studyList) {
        return GetMyStudyInfoResponse.builder()
                .hasChallenge(false)
                .studyList(studyList)
                .build();
    }
}
