package com.togedy.togedy_server_v2.domain.study.enums;

import java.util.Arrays;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyTier {
    BRONZE1("Bronze1", 0L),
    BRONZE2("Bronze2", 50_00000000L),
    BRONZE3("Bronze3", 100_00000000L),
    SILVER1("Silver1", 180_00000000L),
    SILVER2("Silver2", 260_00000000L),
    SILVER3("Silver3", 350_00000000L),
    GOLD1("Gold1", 450_00000000L),
    GOLD2("Gold2", 600_00000000L),
    GOLD3("Gold3", 800_00000000L),
    MASTER("Master", 1050_00000000L),
    LEGEND("Legend", 1600_00000000L);

    private final String name;
    private final long minScore;

    public static StudyTier fromScore(long score) {
        return Arrays.stream(StudyTier.values())
                .sorted(Comparator.comparing(StudyTier::getMinScore).reversed())
                .filter(studyTier -> score >= studyTier.minScore)
                .findFirst()
                .orElse(StudyTier.BRONZE1);
    }
}
