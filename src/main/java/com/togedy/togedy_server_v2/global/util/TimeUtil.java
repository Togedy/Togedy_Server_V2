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

    /**
     * 시작 날짜/시간과 종료 날짜/시간 사이의 경과 시간을 초 단위로 계산한다.
     * <p>
     * 종료 날짜가 {@code null}인 경우 0을 반환한다. 시간 정보가 {@code null}인 경우 해당 날짜의 시작 시각을 기준으로 계산한다.
     * </p>
     *
     * @return 경과 시간 (초)
     */
    public static long calculateDurationInSeconds(
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

    public static int calculateDaysUntil(LocalDate startDate) {
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), startDate);
        return (int) remainingDays;
    }

    /**
     * 기준 시각으로부터 현재까지 경과된 시간을 사람이 읽기 쉬운 형태로 변환한다.
     * <p>
     * 변환 규칙은 다음과 같다.
     * <ul>
     *     <li>10분 미만: "방금 전"</li>
     *     <li>1시간 미만: 10분 단위로 반올림된 분 표시</li>
     *     <li>24시간 미만: 시간 단위 표시</li>
     *     <li>24시간 이상: {@code null} 반환</li>
     * </ul>
     * </p>
     *
     * @param time 기준 시각
     * @return 경과 시간 문자열, 표시할 수 없는 경우 {@code null}
     */
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

    /**
     * 목표 시간 대비 현재 학습 시간의 달성률을 퍼센트(%) 단위로 계산한다.
     * <p>
     * 달성률은 {@code (currentTime / goalTime) * 100}으로 계산되며, 최대값은 100으로 제한된다.
     * </p>
     *
     * @param currentTime 현재 학습 시간 (초)
     * @param goalTime    목표 학습 시간 (초)
     * @return 달성률 (0~100)
     */
    public static int calculateAchievement(Long currentTime, Long goalTime) {
        int percent = (int) ((double) currentTime / goalTime * 100);
        return Math.min(percent, 100);
    }

    public static String formatSecondsToHms(Long second) {
        if (second == null) {
            return "00:00:00";
        }

        long hours = second / 3600;
        long minutes = (second % 3600) / 60;
        long seconds = second % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long calculateStudySeconds(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return 0L;
        }
        return Math.max(0L, Duration.between(startTime, endTime).getSeconds());
    }

    public static LocalDateTime startOfStudyDay(LocalDateTime now) {
        return startOfStudyDay(now, LocalTime.of(5, 0));
    }

    public static LocalDateTime startOfStudyDay(LocalDateTime now, LocalTime boundaryTime) {
        LocalDateTime todayStart = now.toLocalDate().atTime(boundaryTime);
        if (now.isBefore(todayStart)) {
            return todayStart.minusDays(1);
        }
        return todayStart;
    }

    public static LocalDateTime endOfStudyDay(LocalDateTime now) {
        return startOfStudyDay(now).plusDays(1);
    }

    public static Long convertHoursToSeconds(int hour) {
        return hour * 3600L;
    }

    public static LocalDate startOfMonthsAgo(int monthsAgo) {
        return YearMonth.now().minusMonths(monthsAgo).atDay(1);
    }

    public static LocalDate startOfNextMonth() {
        return YearMonth.now().plusMonths(1).atDay(1);
    }

    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    public static LocalDateTime startOfTomorrow() {
        return LocalDate.now().plusDays(1).atStartOfDay();
    }


}
