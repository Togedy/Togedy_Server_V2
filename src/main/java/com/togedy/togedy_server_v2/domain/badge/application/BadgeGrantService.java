package com.togedy.togedy_server_v2.domain.badge.application;

import com.togedy.togedy_server_v2.domain.badge.dao.BadgeRepository;
import com.togedy.togedy_server_v2.domain.badge.dao.HolidayRepository;
import com.togedy.togedy_server_v2.domain.badge.entity.Badge;
import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import com.togedy.togedy_server_v2.domain.badge.enums.BadgeType;
import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private static final List<BadgeType> WEEKEND_TYPES = List.of(
            BadgeType.WEEKEND_20,
            BadgeType.WEEKEND_10
    );

    // 직전 공부일로부터 이 일수 이상 지난 뒤 공부하면 복귀로 본다.
    private static final int COMEBACK_ABSENCE_DAYS = 30;

    private final BadgeRepository badgeRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final HolidayRepository holidayRepository;

    /**
     * 타이머를 종료한 과목의 누적 공부 시간으로 아직 받지 않은 한우물왕 단계를 지급한다.
     * <p>
     * 타이머 종료로 누적 시간이 늘어나는 과목은 방금 종료한 과목뿐이므로 그 과목만 판정한다.
     * 모든 단계를 이미 받았으면 공부 시간을 조회하지 않는다.
     * </p>
     *
     * @param userId         판정 대상 유저 ID
     * @param studySubjectId 타이머를 종료한 과목 ID
     * @param studyDate      타이머 종료 시각의 학습일
     */
    public void grantOneSubjectBadges(Long userId, Long studySubjectId, LocalDate studyDate) {
        List<BadgeType> remainingTypes = findRemainingOneSubjectTypes(userId);
        if (remainingTypes.isEmpty()) {
            return;
        }

        long studyTime = studyTimeRepository.sumStudyTimeByStudySubjectId(studySubjectId);
        remainingTypes.stream()
                .filter(type -> studyTime >= toRequiredSeconds(type))
                .forEach(type -> saveBadge(createOneSubjectBadge(userId, type, studyDate)));
    }

    /**
     * 판정 학습일이 일요일이면 해당 주 토·일 합계로 주말 파이터 뱃지를 지급한다.
     * <p>
     * 주말 합계는 05:00 집계가 끝난 일별 공부 시간을 사용한다. 기준을 넘은 단계 중 가장 높은 단계 하나만 지급하며, periodKey가 해당 주 토요일이므로 매주 다시 받을 수 있다.
     * </p>
     *
     * @param studyDate 판정 대상 학습일
     */
    public void grantWeekendBadges(LocalDate studyDate) {
        if (studyDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            return;
        }

        LocalDate saturday = studyDate.minusDays(1);
        long minStudyTime = toRequiredSeconds(WEEKEND_TYPES.get(WEEKEND_TYPES.size() - 1));

        dailyStudySummaryRepository.findUserStudyTimesByPeriodAtLeast(saturday, studyDate, minStudyTime)
                .forEach(userStudyTime -> findWeekendType(userStudyTime.getStudyTime())
                        .ifPresent(type -> saveBadge(Badge.builder()
                                .userId(userStudyTime.getUserId())
                                .type(type)
                                .date(studyDate)
                                .build())));
    }

    /**
     * 판정 학습일에 끝난 연휴를 대상으로 연휴 파이터 뱃지를 지급한다.
     * <p>
     * 연휴 기간의 모든 학습일에 기준 시간(하루 4시간) 이상 공부한 유저가 받는다.
     * 연휴명은 지급 시점의 이름으로 저장하며, periodKey는 연휴 시작일이다.
     * </p>
     *
     * @param studyDate 판정 대상 학습일
     */
    public void grantHolidayBadges(LocalDate studyDate) {
        for (Holiday holiday : holidayRepository.findAllByEndDate(studyDate)) {
            findUserIdsStudiedEveryHoliday(holiday).forEach(userId -> saveBadge(Badge.builder()
                    .userId(userId)
                    .type(BadgeType.HOLIDAY)
                    .detail(holiday.getName())
                    .date(holiday.getStartDate())
                    .build()));
        }
    }

    /**
     * 타이머 종료 시점까지의 해당 월 누적 공부 시간으로 월간 파이터 뱃지를 지급한다.
     * <p>
     * 타이머 종료 시 일별 공부 시간이 바로 갱신되므로 일별 합계로 월 누적을 계산한다.
     * 해당 월 뱃지를 이미 받았으면 공부 시간을 조회하지 않는다.
     * </p>
     *
     * @param userId    판정 대상 유저 ID
     * @param studyDate 타이머 종료 시각의 학습일
     */
    public void grantMonthlyBadge(Long userId, LocalDate studyDate) {
        String periodKey = BadgeType.MONTHLY_50.getPeriodType().toKey(studyDate);
        if (badgeRepository.existsByUserIdAndTypeAndPeriodKey(userId, BadgeType.MONTHLY_50, periodKey)) {
            return;
        }

        long studyTime = dailyStudySummaryRepository.sumStudyTimeByUserIdAndPeriod(
                userId, studyDate.withDayOfMonth(1), studyDate);
        if (studyTime >= toRequiredSeconds(BadgeType.MONTHLY_50)) {
            saveBadge(createMonthlyBadge(userId, studyDate));
        }
    }

    /**
     * 판정 학습일이 그 달의 마지막 날이면 타이머 종료 시 받지 못한 유저에게 월간 파이터 뱃지를 지급한다.
     * <p>
     * 월말 05:00을 넘겨 실행 중이던 타이머의 지난달 몫은 05:00 집계로만 반영되어 타이머 종료 시 판정에서 빠지므로, 05:00 집계가 끝난 뒤 한 번 더 판정한다.
     * </p>
     *
     * @param studyDate 판정 대상 학습일
     */
    public void grantMonthlyBadges(LocalDate studyDate) {
        if (studyDate.plusDays(1).getDayOfMonth() != 1) {
            return;
        }

        Set<Long> grantedUserIds = badgeRepository.findUserIdsByTypeAndPeriodKey(
                BadgeType.MONTHLY_50, BadgeType.MONTHLY_50.getPeriodType().toKey(studyDate));
        dailyStudySummaryRepository.findUserStudyTimesByPeriodAtLeast(
                        studyDate.withDayOfMonth(1), studyDate, toRequiredSeconds(BadgeType.MONTHLY_50))
                .stream()
                .filter(userStudyTime -> !grantedUserIds.contains(userStudyTime.getUserId()))
                .forEach(userStudyTime -> saveBadge(createMonthlyBadge(userStudyTime.getUserId(), studyDate)));
    }

    /**
     * 판정 학습일에 복귀한 유저를 대상으로 복귀 뱃지를 지급한다.
     * <p>
     * 판정 학습일에 공부했고, 직전 공부일이 판정 학습일보다 30일 이상 앞선 유저가 복귀한 것으로 본다.
     * 이전 공부 기록이 없는 유저는 제외하며, 복귀 뱃지는 한 번만 지급한다.
     * </p>
     *
     * @param studyDate 판정 대상 학습일
     */
    public void grantComebackBadges(LocalDate studyDate) {
        List<Long> returnedUserIds = dailyStudySummaryRepository.findReturnedUserIds(
                studyDate, studyDate.minusDays(COMEBACK_ABSENCE_DAYS));
        if (returnedUserIds.isEmpty()) {
            return;
        }

        Set<Long> grantedUserIds = badgeRepository.findUserIdsByTypeAndUserIdIn(BadgeType.COMEBACK, returnedUserIds);
        returnedUserIds.stream()
                .filter(userId -> !grantedUserIds.contains(userId))
                .forEach(userId -> saveBadge(Badge.builder()
                        .userId(userId)
                        .type(BadgeType.COMEBACK)
                        .date(studyDate)
                        .build()));
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
     * 한우물왕 뱃지를 생성한다.
     *
     * @param userId    지급 대상 유저 ID
     * @param type      지급할 한우물왕 단계
     * @param studyDate 판정 대상 학습일
     * @return 저장 전 뱃지
     */
    private Badge createOneSubjectBadge(Long userId, BadgeType type, LocalDate studyDate) {
        return Badge.builder()
                .userId(userId)
                .type(type)
                .date(studyDate)
                .build();
    }

    /**
     * 지급 시점이 아닌 판정 대상 월(예: "10월")을 detail에 담은 월간 파이터 뱃지를 생성한다.
     *
     * @param userId    지급 대상 유저 ID
     * @param studyDate 판정 대상 월에 속한 학습일
     * @return 저장 전 뱃지
     */
    private Badge createMonthlyBadge(Long userId, LocalDate studyDate) {
        return Badge.builder()
                .userId(userId)
                .type(BadgeType.MONTHLY_50)
                .detail(studyDate.getMonthValue() + "월")
                .date(studyDate)
                .build();
    }

    /**
     * 주말 합계 공부 시간으로 받을 수 있는 가장 높은 주말 파이터 단계를 찾는다.
     *
     * @param studyTime 토·일 합계 공부 시간 (초)
     * @return 가장 높은 도달 단계, 도달한 단계가 없으면 빈 값
     */
    private Optional<BadgeType> findWeekendType(long studyTime) {
        return WEEKEND_TYPES.stream()
                .filter(type -> studyTime >= toRequiredSeconds(type))
                .findFirst();
    }

    /**
     * 연휴 기간의 모든 학습일에 하루 기준 시간 이상 공부한 유저를 조회한다.
     *
     * @param holiday 판정 대상 연휴
     * @return 모든 날 기준 시간을 채운 유저 ID 목록
     */
    private List<Long> findUserIdsStudiedEveryHoliday(Holiday holiday) {
        long days = ChronoUnit.DAYS.between(holiday.getStartDate(), holiday.getEndDate()) + 1;

        return dailyStudySummaryRepository.findUserIdsStudiedEveryDayAtLeast(
                holiday.getStartDate(),
                holiday.getEndDate(),
                toRequiredSeconds(BadgeType.HOLIDAY),
                days
        );
    }

    /**
     * 뱃지 단계의 기준 시간을 초 단위로 변환한다.
     *
     * @param type 뱃지 단계
     * @return 기준 시간 (초)
     */
    private long toRequiredSeconds(BadgeType type) {
        return TimeUtil.convertHoursToSeconds(type.getRequiredHours());
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
