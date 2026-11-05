package com.togedy.togedy_server_v2.domain.badge.scheduler;

import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeGrantService;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BadgeSchedulerTest {

    @Mock
    private BadgeGrantService badgeGrantService;

    @InjectMocks
    private BadgeScheduler badgeScheduler;

    @Test
    @DisplayName("모든 뱃지를 전날 학습일 기준으로 지급한다.")
    void grantBadgesForPreviousStudyDate() {
        LocalDate previousStudyDate = TimeUtil.currentStudyDate().minusDays(1);

        badgeScheduler.grantWeekendBadges();
        badgeScheduler.grantHolidayBadges();
        badgeScheduler.grantMonthlyBadges();

        verify(badgeGrantService).grantWeekendBadges(previousStudyDate);
        verify(badgeGrantService).grantHolidayBadges(previousStudyDate);
        verify(badgeGrantService).grantMonthlyBadges(previousStudyDate);
    }
}
