package com.togedy.togedy_server_v2.domain.badge.enums;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        @Override
        public String toKey(LocalDate date) {
            return date.toString();
        }
    };

    public abstract String toKey(LocalDate date);
}
