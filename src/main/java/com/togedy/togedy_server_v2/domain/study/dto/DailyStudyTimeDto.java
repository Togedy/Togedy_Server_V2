package com.togedy.togedy_server_v2.domain.study.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyStudyTimeDto {
    private Long userId;
    private LocalDateTime date;
    private Long studyTime;
}
