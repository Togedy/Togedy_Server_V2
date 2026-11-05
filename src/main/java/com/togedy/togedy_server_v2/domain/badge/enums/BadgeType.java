package com.togedy.togedy_server_v2.domain.badge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeType {
    ONE_SUBJECT_30("한우물왕(30h)", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/one_subject_30.png",
            30, PeriodType.ONCE),
    ONE_SUBJECT_50("한우물왕(50h)", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/one_subject_50.png",
            50, PeriodType.ONCE),
    ONE_SUBJECT_100("한우물왕(100h)", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/one_subject_100.png",
            100, PeriodType.ONCE),
    ONE_SUBJECT_300("한우물왕(300h)", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/one_subject_300.png",
            300, PeriodType.ONCE),
    WEEKEND_10("10시간 주말 파이터", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/weekend_10.png",
            10, PeriodType.WEEKEND),
    WEEKEND_20("20시간 주말 파이터", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/weekend_20.png",
            20, PeriodType.WEEKEND),
    HOLIDAY("연휴 파이터", "", "", 4, PeriodType.HOLIDAY),   // 하루 기준 4시간
    MONTHLY_50("파이터", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/monthly_%d.png",
            50,
            PeriodType.MONTH),   // imageUrl의 %d에 대상 월이 들어감
    COMEBACK("돌아왔구나", "",
            "https://togedy-prod-s3-292967571397-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/badge/static/welcome_back.png",
            0, PeriodType.ONCE);

    private final String name;
    private final String description;
    private final String imageUrl;
    private final int requiredHours;
    private final PeriodType periodType;

}
