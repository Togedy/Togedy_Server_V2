package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudySearchResponse {

    private Boolean hasNext;

    private List<StudySearchDto> studyList;

    public static GetStudySearchResponse of(boolean hasNext, List<StudySearchDto> studyList) {
        return GetStudySearchResponse.builder()
                .hasNext(hasNext)
                .studyList(studyList)
                .build();
    }

}
