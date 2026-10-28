package com.togedy.togedy_server_v2.domain.badge.util;

import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 공부 기록 타임라인을 따라가며 한우물왕 단계별로 기준 시간에 가장 먼저 도달한 과목을 찾는다.
 */
public class OneSubjectTimeline {

    private OneSubjectTimeline() {
    }

    /**
     * 단계별로 기준 시간에 가장 먼저 도달한 과목을 찾는다.
     * <p>
     * 삭제된 과목의 공부 시간은 해당 과목의 누적에만 쓰이고, 도달 후보에서는 제외되어 다음으로 도달한 과목이 선정된다.
     * 기준 시각 이후의 공부 시간은 계산하지 않는다.
     * </p>
     *
     * @param studyTimes       종료된 공부 기록
     * @param activeSubjectIds 판정 시점에 삭제되지 않은 과목 ID
     * @param types            판정 대상 한우물왕 단계
     * @param endTime          판정 기준 시각
     * @return 단계별 최초 도달 과목 ID, 도달한 과목이 없는 단계는 포함하지 않는다.
     */
    public static Map<BadgeType, Long> findFirstReachedSubjects(
            List<StudyTime> studyTimes,
            Set<Long> activeSubjectIds,
            List<BadgeType> types,
            LocalDateTime endTime
    ) {
        Map<Long, Long> accumulatedSeconds = new HashMap<>();
        Map<BadgeType, ReachedSubject> reachedSubjects = new EnumMap<>(BadgeType.class);

        for (StudyTime studyTime : studyTimes) {
            Long subjectId = studyTime.getStudySubjectId();
            long before = accumulatedSeconds.getOrDefault(subjectId, 0L);
            long after = before + calculateStudySecondsBefore(studyTime, endTime);
            accumulatedSeconds.put(subjectId, after);

            if (activeSubjectIds.contains(subjectId)) {
                recordReachedTypes(studyTime, before, after, types, reachedSubjects);
            }
        }

        return toSubjectIds(reachedSubjects);
    }

    private static long calculateStudySecondsBefore(StudyTime studyTime, LocalDateTime endTime) {
        LocalDateTime effectiveEnd = studyTime.getEndTime().isBefore(endTime) ? studyTime.getEndTime() : endTime;
        return TimeUtil.calculateStudySeconds(studyTime.getStartTime(), effectiveEnd);
    }

    /**
     * 이번 공부 기록으로 기준 시간을 넘긴 단계마다 도달 시각을 계산해, 기존 후보보다 이르면 후보를 교체한다.
     */
    private static void recordReachedTypes(
            StudyTime studyTime,
            long before,
            long after,
            List<BadgeType> types,
            Map<BadgeType, ReachedSubject> reachedSubjects
    ) {
        for (BadgeType type : types) {
            long requiredSeconds = TimeUtil.convertHoursToSeconds(type.getRequiredHours());
            if (before >= requiredSeconds || after < requiredSeconds) {
                continue;
            }

            ReachedSubject candidate = new ReachedSubject(
                    studyTime.getStudySubjectId(),
                    studyTime.getStartTime().plusSeconds(requiredSeconds - before)
            );
            reachedSubjects.merge(type, candidate, ReachedSubject::earlier);
        }
    }

    private static Map<BadgeType, Long> toSubjectIds(Map<BadgeType, ReachedSubject> reachedSubjects) {
        Map<BadgeType, Long> subjectIds = new EnumMap<>(BadgeType.class);
        reachedSubjects.forEach((type, reachedSubject) -> subjectIds.put(type, reachedSubject.subjectId()));
        return subjectIds;
    }

    private record ReachedSubject(Long subjectId, LocalDateTime reachedTime) {

        private ReachedSubject earlier(ReachedSubject other) {
            return other.reachedTime.isBefore(reachedTime) ? other : this;
        }
    }
}
