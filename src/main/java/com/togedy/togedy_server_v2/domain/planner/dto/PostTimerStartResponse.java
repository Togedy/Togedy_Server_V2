package com.togedy.togedy_server_v2.domain.planner.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostTimerStartResponse {

    private Long timerId;
    private LocalDateTime startTime;

    public static PostTimerStartResponse of(Long timerId, LocalDateTime startTime) {
        return PostTimerStartResponse.builder()
                .timerId(timerId)
                .startTime(startTime)
                .build();
    }
}
