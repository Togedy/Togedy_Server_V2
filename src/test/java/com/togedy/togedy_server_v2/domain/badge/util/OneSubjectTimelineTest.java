package com.togedy.togedy_server_v2.domain.badge.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OneSubjectTimelineTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 6, 0);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2026, 12, 31, 5, 0);
    private static final List<BadgeType> TYPES = List.of(BadgeType.ONE_SUBJECT_30, BadgeType.ONE_SUBJECT_50);

    @Test
    @DisplayName("단계마다 기준 시간에 가장 먼저 도달한 과목이 선정된다.")
    void eachTierGoesToFirstReachedSubject() {
        List<StudyTime> studyTimes = List.of(
                studyTime(1L, 0, 30),   // 과목 1: 30시간 도달
                studyTime(2L, 1, 49),   // 과목 2: 49시간
                studyTime(2L, 3, 1),    // 과목 2: 50시간 도달
                studyTime(1L, 4, 20)    // 과목 1: 50시간 도달 (과목 2보다 늦음)
        );

        Map<BadgeType, Long> result = OneSubjectTimeline.findFirstReachedSubjects(
                studyTimes, Set.of(1L, 2L), TYPES, END_TIME);

        assertThat(result).containsEntry(BadgeType.ONE_SUBJECT_30, 1L)
                .containsEntry(BadgeType.ONE_SUBJECT_50, 2L);
    }

    @Test
    @DisplayName("삭제된 과목이 먼저 도달하면 다음으로 도달한 과목이 선정된다.")
    void deletedSubjectIsSkipped() {
        List<StudyTime> studyTimes = List.of(
                studyTime(1L, 0, 30),
                studyTime(2L, 2, 30)
        );

        Map<BadgeType, Long> result = OneSubjectTimeline.findFirstReachedSubjects(
                studyTimes, Set.of(2L), TYPES, END_TIME);

        assertThat(result).containsOnly(Map.entry(BadgeType.ONE_SUBJECT_30, 2L));
    }

    @Test
    @DisplayName("한 기록 안에서 도달한 경우 도달 시각으로 순서를 비교한다.")
    void comparesReachedTimeWithinRecord() {
        List<StudyTime> studyTimes = List.of(
                studyTime(1L, 0, 29),
                studyTime(2L, 1, 29),
                studyTime(2L, 3, 10),   // 과목 2: 시작 1시간 후 30시간 도달
                studyTime(1L, 2, 1)     // 과목 1: 시작 1시간 후 30시간 도달, 과목 2보다 이른 시각
        );

        Map<BadgeType, Long> result = OneSubjectTimeline.findFirstReachedSubjects(
                studyTimes, Set.of(1L, 2L), TYPES, END_TIME);

        assertThat(result).containsEntry(BadgeType.ONE_SUBJECT_30, 1L);
    }

    @Test
    @DisplayName("판정 기준 시각 이후의 공부 시간은 계산하지 않는다.")
    void ignoresStudyAfterEndTime() {
        StudyTime studyTime = StudyTime.builder()
                .userId(1L)
                .studySubjectId(1L)
                .startTime(BASE)
                .endTime(BASE.plusHours(30))
                .build();

        Map<BadgeType, Long> result = OneSubjectTimeline.findFirstReachedSubjects(
                List.of(studyTime), Set.of(1L), TYPES, BASE.plusHours(29));

        assertThat(result).isEmpty();
    }

    private StudyTime studyTime(Long subjectId, int startDay, int hours) {
        LocalDateTime startTime = BASE.plusDays(startDay);
        return StudyTime.builder()
                .userId(1L)
                .studySubjectId(subjectId)
                .startTime(startTime)
                .endTime(startTime.plusHours(hours))
                .build();
    }
}
