package com.togedy.togedy_server_v2.domain.planner.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDailyPlannerStatisticsResponse {

    private int daysSinceLastStudy;
    private int currentStreakDays;
    private List<String> weeklyReview;
    private List<Integer> monthlyReview;

    public static GetDailyPlannerStatisticsResponse of(
            int daysSinceLastStudy,
            int currentStreakDays,
            List<String> weeklyReview,
            List<Integer> monthlyReview
    ) {
        return GetDailyPlannerStatisticsResponse.builder()
                .daysSinceLastStudy(daysSinceLastStudy)
                .currentStreakDays(currentStreakDays)
                .weeklyReview(weeklyReview)
                .monthlyReview(monthlyReview)
                .build();
    }
}
