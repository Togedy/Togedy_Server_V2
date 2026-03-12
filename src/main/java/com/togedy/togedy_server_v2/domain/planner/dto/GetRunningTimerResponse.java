package com.togedy.togedy_server_v2.domain.planner.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRunningTimerResponse {

    private Long timerId;
    private Long subjectId;
    private LocalDateTime startTime;

    public static GetRunningTimerResponse of(Long timerId, Long subjectId, LocalDateTime startTime) {
        return GetRunningTimerResponse.builder()
                .timerId(timerId)
                .subjectId(subjectId)
                .startTime(startTime)
                .build();
    }
}
