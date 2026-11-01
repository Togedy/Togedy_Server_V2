package com.togedy.togedy_server_v2.domain.badge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeType {
    ONE_SUBJECT_30("한우물왕(30h)", "", "", 30, PeriodType.ONCE),
    ONE_SUBJECT_50("한우물왕(50h)", "", "", 50, PeriodType.ONCE),
    ONE_SUBJECT_100("한우물왕(100h)", "", "", 100, PeriodType.ONCE),
    ONE_SUBJECT_300("한우물왕(300h)", "", "", 300, PeriodType.ONCE),
    WEEKEND_10("10시간 주말 파이터", "", "", 10, PeriodType.WEEKEND),
    WEEKEND_20("20시간 주말 파이터", "", "", 20, PeriodType.WEEKEND),
    HOLIDAY("연휴 파이터", "", "", 4, PeriodType.HOLIDAY),   // 하루 기준 4시간
    MONTHLY_50("파이터", "", "", 50, PeriodType.MONTH),
    COMEBACK("돌아왔구나", "", "", 0, PeriodType.ONCE);

    private final String name;
    private final String description;
    private final String imageUrl;
    private final int requiredHours;
    private final PeriodType periodType;

}
