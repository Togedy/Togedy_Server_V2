package com.togedy.togedy_server_v2.domain.study.dto;

import java.time.YearMonth;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyStudyTimeDto {

    private Integer year;

    private Integer month;

    private List<Integer> studyTimeList;

    public static MonthlyStudyTimeDto of(YearMonth yearMonth, List<Integer> studyTimeList) {
        return MonthlyStudyTimeDto.builder()
                .year(yearMonth.getYear())
                .month(yearMonth.getMonthValue())
                .studyTimeList(studyTimeList)
                .build();
    }
}
