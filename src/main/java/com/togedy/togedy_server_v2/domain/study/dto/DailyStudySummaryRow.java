package com.togedy.togedy_server_v2.domain.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DailyStudySummaryRow {

    private Long studyId;
    private Long userId;
    private Long studyTime;

}
