package com.togedy.togedy_server_v2.global.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateTimeUtils {

    public static long durationInSeconds(
            LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime
    ) {
        if (endDate == null) {
            return 0L;
        }
        LocalDateTime start = startDate.atStartOfDay();
        if (startTime != null) {
            start = LocalDateTime.of(startDate, startTime);
        }

        LocalDateTime end = endDate.atStartOfDay();
        if (endTime != null) {
            end = LocalDateTime.of(endDate, endTime);
        }

        return Duration.between(start, end).getSeconds();
    }

    public static LocalDateTime toStartDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, Objects.requireNonNullElse(time, LocalTime.MIN));
    }

    public static int calculateRemainingDays(LocalDate startDate) {
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), startDate);
        return (int) remainingDays;
    }
}