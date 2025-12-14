package com.togedy.togedy_server_v2.global.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TimeUtil {

    private TimeUtil() {
    }

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

    public static String formatTimeAgo(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(time, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = seconds / 3600;

        if (seconds < 600) {
            return "방금 전";
        }

        if (seconds < 3600) {
            long roundedMinutes = (minutes / 10) * 10;
            return roundedMinutes + "분 전";
        }

        if (seconds < 86400) {
            return hours + "시간 전";
        }

        return null;
    }

    public static int calculateAchievement(Long currentTime, Long goalTime) {
        int percent = (int) ((double) currentTime / goalTime * 100);
        return Math.min(percent, 100);
    }

    public static String toTimeFormat(Long second) {
        if (second == null) {
            return "00:00:00";
        }

        long hours = second / 3600;
        long minutes = (second % 3600) / 60;
        long seconds = second % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static Long convertHoursToSeconds(int hour) {
        return hour * 3600L;
    }

    public static LocalDateTime startOfMonthsAgo(int monthsAgo) {
        return YearMonth.now().minusMonths(monthsAgo).atDay(1).atStartOfDay();
    }

    public static LocalDateTime startOfNextMonth() {
        return YearMonth.now().plusMonths(1).atDay(1).atStartOfDay();
    }

}