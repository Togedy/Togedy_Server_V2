package com.togedy.togedy_server_v2.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateTimeUtils {

    public static long durationInSeconds(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            return 0L;
        }
        if (endDate == null) {
            return 0L;
        }
        return Duration.between(startDate, endDate).getSeconds();
    }
}