package com.togedy.togedy_server_v2.domain.planner.scheduler;

import com.togedy.togedy_server_v2.domain.planner.application.TimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyTimeScheduler {

    private final TimerService timerService;

    @Scheduled(fixedDelay = 60_000)
    public void cleanupExpiredTimers() {
        try {
            timerService.cleanup();
        } catch (Exception e) {
            log.error("타이머 정리 실패");
        }
    }
}
