package com.togedy.togedy_server_v2.domain.planner.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostTimerStopRequest {

    @NotNull
    private Long timerId;
}
