package com.togedy.togedy_server_v2.global.fixtures;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDateTime;

public class StudyTimeFixture {

    private StudyTimeFixture() {
    }

    public static StudyTime createRunningStudyTime(Long userId) {
        return StudyTime.builder()
                .userId(userId)
                .startTime(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endTime(null)
                .isRunning(true)
                .studySubjectId(1L)
                .build();
    }

    public static StudyTime createEndedStudyTime(Long userId) {
        return StudyTime.builder()
                .userId(userId)
                .startTime(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endTime(LocalDateTime.of(2026, 1, 1, 1, 0))
                .isRunning(false)
                .studySubjectId(1L)
                .build();
    }
}
