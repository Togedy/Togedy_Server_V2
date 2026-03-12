package com.togedy.togedy_server_v2.domain.planner.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DailyStudySummaryTest {

    @Test
    void 공부시간이_null이면_0부터_누적한다() {
        DailyStudySummary dailyStudySummary = DailyStudySummary.builder()
                .userId(1L)
                .studyTime(null)
                .date(LocalDate.of(2026, 3, 10))
                .build();

        dailyStudySummary.addStudyTime(300L);

        assertThat(dailyStudySummary.getStudyTime()).isEqualTo(300L);
    }

    @Test
    void 음수_공부시간은_누적할_수_없다() {
        DailyStudySummary dailyStudySummary = DailyStudySummary.builder()
                .userId(1L)
                .studyTime(100L)
                .date(LocalDate.of(2026, 3, 10))
                .build();

        assertThatThrownBy(() -> dailyStudySummary.addStudyTime(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("additionalStudyTime must be >= 0");
    }
}
