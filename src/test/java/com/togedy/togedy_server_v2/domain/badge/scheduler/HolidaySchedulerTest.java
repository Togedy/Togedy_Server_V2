package com.togedy.togedy_server_v2.domain.badge.scheduler;

import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.badge.application.HolidayService;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidaySchedulerTest {

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private HolidayScheduler holidayScheduler;

    @Test
    @DisplayName("올해와 다음 해의 연휴를 생성한다.")
    void generateRuleHolidaysForThisAndNextYear() {
        int year = TimeUtil.todayInStudyZone().getYear();

        holidayScheduler.generateRuleHolidays();

        verify(holidayService).generateRuleHolidays(year);
        verify(holidayService).generateRuleHolidays(year + 1);
    }
}
