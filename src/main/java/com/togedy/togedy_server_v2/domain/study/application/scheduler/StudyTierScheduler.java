package com.togedy.togedy_server_v2.domain.study.application.scheduler;

import com.togedy.togedy_server_v2.domain.study.application.StudyTierService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StudyTierScheduler {

    private StudyTierService studyTierService;

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void updateStudyTier() {
        studyTierService.applyStudyTier();
    }
}
