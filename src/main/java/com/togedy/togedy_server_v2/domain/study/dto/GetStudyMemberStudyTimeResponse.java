package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetStudyMemberStudyTimeResponse {

    private Integer studyTimeCount;
    private List<MonthlyStudyTimeDto> monthlyStudyTimeList;

    public static GetStudyMemberStudyTimeResponse of(
            int studyTimeCount, List<MonthlyStudyTimeDto> monthlyStudyTimeList
    )
    {
        return GetStudyMemberStudyTimeResponse.builder()
                .studyTimeCount(studyTimeCount)
                .monthlyStudyTimeList(monthlyStudyTimeList)
                .build();
    }
}
