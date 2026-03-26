package com.togedy.togedy_server_v2.domain.study.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeGoalTime {

    THREE_HOURS(3L * 3600L),
    FIVE_HOURS(5L * 3600L),
    SEVEN_HOURS(7L * 3600L);

    private final long seconds;

    public static boolean isValid(Long goalTime) {
        if (goalTime == null) {
            return false;
        }

        return Arrays.stream(values())
                .anyMatch(value -> value.seconds == goalTime);
    }
}
