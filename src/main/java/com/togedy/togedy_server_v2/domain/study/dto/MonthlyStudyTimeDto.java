package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyStudyTimeDto {

    private Integer year;

    private Integer month;

    private List<Integer> studyTimeList;

    public static MonthlyStudyTimeDto of(int year, int month, List<Integer> studyTimeList) {
        return MonthlyStudyTimeDto.builder()
                .year(year)
                .month(month)
                .studyTimeList(studyTimeList)
                .build();
    }
}
