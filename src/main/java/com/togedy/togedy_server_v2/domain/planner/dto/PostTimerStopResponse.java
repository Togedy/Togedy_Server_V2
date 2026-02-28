package com.togedy.togedy_server_v2.domain.planner.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostTimerStopResponse {

    private Long timerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static PostTimerStopResponse of(Long timerId, LocalDateTime startTime, LocalDateTime endTime) {
        return PostTimerStopResponse.builder()
                .timerId(timerId)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
