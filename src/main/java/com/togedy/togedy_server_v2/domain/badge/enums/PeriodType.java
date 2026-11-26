package com.togedy.togedy_server_v2.domain.badge.enums;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

public enum PeriodType {
    ONCE {
        @Override
        public String toKey(LocalDate date) {
            return "ONCE";
        }
    },
    WEEKEND {
        @Override
        public String toKey(LocalDate date) {
            return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString();
        }
    },
    HOLIDAY {
        // 연휴 뱃지의 기간 키는 날짜가 아닌 연휴 ID이므로 Badge.createHolidayBadge로 생성한다.
        @Override
        public String toKey(LocalDate date) {
            throw new UnsupportedOperationException("연휴 뱃지는 Badge.createHolidayBadge로 생성한다.");
        }
    },
    MONTH {
        @Override
        public String toKey(LocalDate date) {
            return YearMonth.from(date).toString();
        }
    };

    public abstract String toKey(LocalDate date);
}
