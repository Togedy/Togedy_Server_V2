package com.togedy.togedy_server_v2.domain.study.application.scheduler;

import com.togedy.togedy_server_v2.domain.study.application.StudyTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyStudySummaryScheduler {

    private final StudyTierService studyTierService;

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    public void aggregate() {
        studyTierService.aggregateRunningStudyTimes();
    }
}
