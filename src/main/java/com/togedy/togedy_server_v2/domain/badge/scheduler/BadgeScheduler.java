package com.togedy.togedy_server_v2.domain.badge.scheduler;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeGrantService;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 05:00 일별 공부 시간 집계가 끝난 뒤 전날 학습일 기준으로 뱃지를 지급한다.
 * <p>
 * 뱃지마다 작업을 분리해 한 작업이 실패해도 나머지 작업은 실행되고, 실패는 작업별로 알림이 전송된다.
 * 뱃지 저장마다 개별 트랜잭션을 사용해야 하므로 트랜잭션으로 감싸지 않는다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class BadgeScheduler {

    private static final String BADGE_GRANT_CRON = "0 10 5 * * *";

    private final BadgeGrantService badgeGrantService;

    @Scheduled(cron = BADGE_GRANT_CRON, zone = "Asia/Seoul")
    public void grantOneSubjectBadges() {
        badgeGrantService.grantOneSubjectBadges(previousStudyDate());
    }

    @Scheduled(cron = BADGE_GRANT_CRON, zone = "Asia/Seoul")
    public void grantWeekendBadges() {
        badgeGrantService.grantWeekendBadges(previousStudyDate());
    }

    @Scheduled(cron = BADGE_GRANT_CRON, zone = "Asia/Seoul")
    public void grantHolidayBadges() {
        badgeGrantService.grantHolidayBadges(previousStudyDate());
    }

    @Scheduled(cron = BADGE_GRANT_CRON, zone = "Asia/Seoul")
    public void grantMonthlyBadges() {
        badgeGrantService.grantMonthlyBadges(previousStudyDate());
    }

    @Scheduled(cron = BADGE_GRANT_CRON, zone = "Asia/Seoul")
    public void grantComebackBadges() {
        badgeGrantService.grantComebackBadges(previousStudyDate());
    }

    /**
     * 판정 대상인 전날 학습일을 구한다.
     *
     * @return 현재 학습일의 전날
     */
    private LocalDate previousStudyDate() {
        return TimeUtil.currentStudyDate().minusDays(1);
    }
}
