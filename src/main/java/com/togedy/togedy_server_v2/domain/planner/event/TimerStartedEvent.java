package com.togedy.togedy_server_v2.domain.planner.event;

import java.time.LocalDateTime;

public record TimerStartedEvent(Long userId, LocalDateTime startTime) {
}
