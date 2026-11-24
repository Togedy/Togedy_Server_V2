package com.togedy.togedy_server_v2.domain.badge.scheduler;

import com.togedy.togedy_server_v2.domain.badge.application.HolidayService;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 날짜 규칙이 있는 연휴 종류로 올해와 다음 해의 연휴를 생성한다.
 * <p>
 * 이미 생성된 연휴는 건너뛰므로 매일 실행해, 실행을 놓친 날이 있어도 다음 실행에서 채운다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private static final String HOLIDAY_GENERATE_CRON = "0 0 4 * * *";

    private final HolidayService holidayService;

    @Scheduled(cron = HOLIDAY_GENERATE_CRON, zone = "Asia/Seoul")
    public void generateRuleHolidays() {
        int year = TimeUtil.todayInStudyZone().getYear();
        LocalDate studyDate = TimeUtil.currentStudyDate();
        holidayService.generateRuleHolidays(year, studyDate);
        holidayService.generateRuleHolidays(year + 1, studyDate);
    }
}
