package com.togedy.togedy_server_v2.domain.study.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyStudyTimeDto {
    private Long userId;
    private LocalDate date;
    private Long studyTime;
}
