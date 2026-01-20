package com.togedy.togedy_server_v2.domain.study.enums;

import java.util.Arrays;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyTier {
    BRONZE1("Bronze1", 0),
    BRONZE2("Bronze2", 50),
    BRONZE3("Bronze3", 100),
    SILVER1("Silver1", 180),
    SILVER2("Silver2", 260),
    SILVER3("Silver3", 350),
    GOLD1("Gold1", 450),
    GOLD2("Gold2", 600),
    GOLD3("Gold3", 800),
    MASTER("Master", 1050),
    LEGEND("Legend", 1600);

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
