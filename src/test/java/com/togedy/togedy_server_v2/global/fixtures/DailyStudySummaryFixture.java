package com.togedy.togedy_server_v2.global.fixtures;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import java.time.LocalDate;

public class DailyStudySummaryFixture {

    private DailyStudySummaryFixture() {
    }

    public static DailyStudySummary createDailyStudySummary() {
        return DailyStudySummary.builder()
                .userId(1L)
                .studyTime(3L * 3600L)
                .date(LocalDate.of(2026, 1, 1))
                .build();
    }

    public static DailyStudySummary createDailyStudySummaryWithUserId(Long userId) {
        return DailyStudySummary.builder()
                .userId(userId)
                .studyTime(3L * 3600L)
                .date(LocalDate.of(2026, 1, 1))
                .build();
    }

    public static DailyStudySummary createDailyStudySummaryWithStudyTime(Long studyTime) {
        return DailyStudySummary.builder()
                .userId(1L)
                .studyTime(studyTime)
                .date(LocalDate.of(2026, 1, 1))
                .build();
    }

    public static DailyStudySummary createDailyStudySummaryWithDate(LocalDate date) {
        return DailyStudySummary.builder()
                .userId(1L)
                .studyTime(3L * 3600L)
                .date(date)
                .build();
    }
}
