package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.badge.util.OneSubjectTimeline;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeGrantService {

    private static final List<BadgeType> ONE_SUBJECT_TYPES = List.of(
            BadgeType.ONE_SUBJECT_30,
            BadgeType.ONE_SUBJECT_50,
            BadgeType.ONE_SUBJECT_100,
            BadgeType.ONE_SUBJECT_300
    );

    private final BadgeRepository badgeRepository;
    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    /**
     * 해당 학습일에 공부한 유저를 대상으로 한우물왕 뱃지를 지급한다.
     * <p>
     * 단계마다 타임라인상 기준 시간에 가장 먼저 도달한 과목이 받으며, 판정 시점에 삭제된 과목은 제외한다.
     * 과목명은 지급 시점의 이름으로 저장한다. 유저 단위로 처리하므로 한 유저의 실패가 다른 유저의 지급을 막지 않는다.
     * </p>
     *
     * @param studyDate 판정 대상 학습일
     */
    public void grantOneSubjectBadges(LocalDate studyDate) {
        LocalDateTime startTime = TimeUtil.startOfStudyDay(studyDate);
        LocalDateTime endTime = startTime.plusDays(1);

        for (Long userId : studyTimeRepository.findStudiedUserIds(startTime, endTime)) {
            try {
                grantUserOneSubjectBadges(userId, studyDate, endTime);
            } catch (Exception e) {
                log.error("한우물왕 뱃지 지급 실패 userId={}", userId, e);
            }
        }
    }

    /**
     * 한 유저에게 아직 받지 않은 한우물왕 단계 중 도달한 단계를 지급한다.
     * <p>
     * 남은 단계가 없거나 가장 낮은 기준에 도달한 활성 과목이 없으면 공부 기록 전체를 조회하지 않고 종료한다.
     * </p>
     *
     * @param userId    판정 대상 유저 ID
     * @param studyDate 판정 대상 학습일
     * @param endTime   판정 기준 시각 (판정 학습일 다음 날 05:00)
     */
    private void grantUserOneSubjectBadges(Long userId, LocalDate studyDate, LocalDateTime endTime) {
        List<BadgeType> remainingTypes = findRemainingOneSubjectTypes(userId);
        if (remainingTypes.isEmpty()) {
            return;
        }

        Map<Long, StudySubject> activeSubjects = findActiveSubjects(userId);
        if (!hasReachableSubject(userId, activeSubjects.keySet(), remainingTypes.get(0), endTime)) {
            return;
        }

        List<StudyTime> studyTimes = studyTimeRepository.findEndedStudyTimesBefore(userId, endTime);
        Map<BadgeType, Long> reachedSubjectIds = OneSubjectTimeline.findFirstReachedSubjects(
                studyTimes, activeSubjects.keySet(), remainingTypes, endTime);

        reachedSubjectIds.forEach((type, subjectId) ->
                saveBadge(createOneSubjectBadge(userId, type, activeSubjects.get(subjectId), studyDate)));
    }

    /**
     * 유저가 아직 받지 않은 한우물왕 단계를 조회한다.
     *
     * @param userId 조회 대상 유저 ID
     * @return 받지 않은 단계 목록, 기준 시간 오름차순
     */
    private List<BadgeType> findRemainingOneSubjectTypes(Long userId) {
        Set<BadgeType> grantedTypes = badgeRepository.findTypesByUserIdAndTypeIn(userId, ONE_SUBJECT_TYPES);
        return ONE_SUBJECT_TYPES.stream()
                .filter(type -> !grantedTypes.contains(type))
                .toList();
    }

    /**
     * 유저의 삭제되지 않은 과목을 조회한다.
     *
     * @param userId 조회 대상 유저 ID
     * @return 과목 ID를 키로 하는 활성 과목 Map
     */
    private Map<Long, StudySubject> findActiveSubjects(Long userId) {
        return studySubjectRepository.findAllByUserId(userId).stream()
                .collect(Collectors.toMap(StudySubject::getId, Function.identity()));
    }

    /**
     * 남은 단계 중 가장 낮은 기준 시간에 도달한 활성 과목이 있는지 확인한다.
     * <p>
     * 공부 기록 전체를 불러오기 전에 과목별 누적 합계로 먼저 걸러내, 지급 가능성이 없는 유저의 조회 비용을 줄인다.
     * </p>
     *
     * @param userId           판정 대상 유저 ID
     * @param activeSubjectIds 활성 과목 ID
     * @param lowestType       남은 단계 중 기준 시간이 가장 낮은 단계
     * @param endTime          판정 기준 시각
     * @return 기준 시간에 도달한 활성 과목이 있으면 {@code true}
     */
    private boolean hasReachableSubject(
            Long userId,
            Set<Long> activeSubjectIds,
            BadgeType lowestType,
            LocalDateTime endTime
    ) {
        long requiredSeconds = TimeUtil.convertHoursToSeconds(lowestType.getRequiredHours());

        return studyTimeRepository.sumStudyTimeBySubjectBefore(userId, endTime).stream()
                .filter(row -> activeSubjectIds.contains(((Number) row[0]).longValue()))
                .anyMatch(row -> ((Number) row[1]).longValue() >= requiredSeconds);
    }

    /**
     * 지급 시점의 과목명을 담은 한우물왕 뱃지를 생성한다.
     *
     * @param userId    지급 대상 유저 ID
     * @param type      지급할 한우물왕 단계
     * @param subject   기준 시간에 가장 먼저 도달한 과목
     * @param studyDate 판정 대상 학습일
     * @return 저장 전 뱃지
     */
    private Badge createOneSubjectBadge(Long userId, BadgeType type, StudySubject subject, LocalDate studyDate) {
        return Badge.builder()
                .userId(userId)
                .type(type)
                .detail(subject.getName())
                .date(studyDate)
                .build();
    }

    /**
     * 뱃지를 저장한다.
     * <p>
     * 배치 재실행이나 동시 실행으로 같은 뱃지가 이미 저장된 경우 유니크 제약 위반을 무시하고 경고 로그만 남긴다.
     * </p>
     *
     * @param badge 저장할 뱃지
     */
    private void saveBadge(Badge badge) {
        try {
            badgeRepository.save(badge);
        } catch (DataIntegrityViolationException e) {
            log.warn("이미 지급된 뱃지 userId={}, type={}, periodKey={}",
                    badge.getUserId(), badge.getType(), badge.getPeriodKey());
        }
    }
}
