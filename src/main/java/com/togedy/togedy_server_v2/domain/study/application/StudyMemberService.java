package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlanRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.Plan;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.DailyPlannerDto;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberPlannerResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberProfileResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberStudyTimeResponse;
import com.togedy.togedy_server_v2.domain.study.dto.MonthlyStudyTimeDto;
import com.togedy.togedy_server_v2.domain.study.dto.PatchPlannerVisibilityRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PlanDto;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.exception.StudyAccessDeniedException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserAccessDeniedException;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final UserStudyRepository userStudyRepository;
    private final UserRepository userRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final StudyCategoryRepository studyCategoryRepository;
    private final PlanRepository planRepository;

    private static final int MONTH_RANGE = 6;

    /**
     * 스터디 멤버의 프로필 정보를 조회한다.
     * <p>
     * 해당 스터디에 속한 사용자만 조회할 수 있으며, 멤버의 기본 정보, 누적 공부 시간, 스터디 참여 경과일을 반환한다.
     * </p>
     *
     * @param studyId  조회 대상 스터디 ID
     * @param memberId 프로필을 조회할 멤버 사용자 ID
     * @param userId   조회를 요청한 사용자 ID
     * @return 스터디 멤버 프로필 조회 DTO
     * @throws StudyAccessDeniedException 요청한 사용자가 해당 스터디에 속해 있지 않은 경우
     * @throws UserNotFoundException      조회 대상 멤버가 존재하지 않는 경우
     * @throws UserStudyNotFoundException 조회 대상 멤버가 해당 스터디에 속해 있지 않은 경우
     */
    public GetStudyMemberProfileResponse findStudyMemberProfile(Long studyId, Long memberId, Long userId) {
        validateUserInStudy(studyId, userId);

        User member = userRepository.findById(memberId).orElseThrow(UserNotFoundException::new);

        Long totalStudyTime = dailyStudySummaryRepository.findTotalStudyTimeByUserId(memberId).orElse(0L);

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        return GetStudyMemberProfileResponse.of(member, TimeUtil.formatSecondsToHms(totalStudyTime),
                userStudy.calculateElapsedDays());
    }

    /**
     * 스터디 멤버의 월별 공부 시간을 조회한다.
     * <p>
     * 해당 스터디에 속한 사용자만 조회할 수 있으며, 최근 6개월(현재 월 포함) 동안의 학습 기록을 월 단위로 집계하여 반환한다.
     * </p>
     *
     * @param studyId  조회 대상 스터디 ID
     * @param memberId 공부 시간을 조회할 멤버 사용자 ID
     * @param userId   조회를 요청한 사용자 ID
     * @return 스터디 멤버 월별 공부 시간 조회 DTO
     * @throws StudyAccessDeniedException 요청한 사용자가 해당 스터디에 속해 있지 않은 경우
     */
    public GetStudyMemberStudyTimeResponse findStudyMemberStudyTime(Long studyId, Long memberId, Long userId) {
        validateUserInStudy(studyId, userId);

        LocalDate start = TimeUtil.startOfMonthsAgo(5);
        LocalDate end = TimeUtil.startOfNextMonth();

        List<DailyStudySummary> dailyStudySummaries =
                dailyStudySummaryRepository.findAllByUserIdAndPeriod(memberId, start, end);

        Map<YearMonth, List<DailyStudySummary>> summariesByMonth = dailyStudySummaries.stream()
                .collect(Collectors.groupingBy(dss -> YearMonth.from(dss.getDate())));

        List<MonthlyStudyTimeDto> monthlyStudyTimeDtoList = new ArrayList<>();

        int studyTimeCount = collectMonthlyStudyTimes(summariesByMonth, monthlyStudyTimeDtoList);

        return GetStudyMemberStudyTimeResponse.of(studyTimeCount, monthlyStudyTimeDtoList);
    }

    /**
     * 스터디 멤버의 오늘 플래너 정보를 조회한다.
     * <p>
     * 해당 스터디에 속한 사용자만 조회할 수 있으며, 멤버의 플래너 공개 여부에 따라 조회 결과가 달라진다.
     * </p>
     * <p>
     * 플래너가 비공개인 경우, 본인 여부 정보만 포함된 응답을 반환한다.
     * </p>
     *
     * @param studyId  조회 대상 스터디 ID
     * @param memberId 플래너를 조회할 멤버 사용자 ID
     * @param userId   조회를 요청한 사용자 ID
     * @return 스터디 멤버 플래너 조회 DTO
     * @throws StudyAccessDeniedException 요청한 사용자가 해당 스터디에 속해 있지 않은 경우
     * @throws UserNotFoundException      조회 대상 멤버가 존재하지 않는 경우
     */
    public GetStudyMemberPlannerResponse findStudyMemberPlanner(Long studyId, Long memberId, Long userId) {
        validateUserInStudy(studyId, userId);

        User member = userRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);

        boolean isMyPlanner = member.getId().equals(userId);

        if (!member.isPlannerVisible()) {
            return GetStudyMemberPlannerResponse.of(isMyPlanner, false);
        }

        LocalDateTime startOfToday = TimeUtil.startOfToday();
        LocalDateTime startOfTomorrow = TimeUtil.startOfTomorrow();

        List<StudyCategory> studyCategories = studyCategoryRepository.findAllByUserId(memberId);

        List<Long> studyCategoryIds = studyCategories.stream()
                .map(StudyCategory::getId)
                .toList();

        List<Plan> todayPlans = planRepository.findAllByStudyCategoryIdsAndPeriod(
                studyCategoryIds,
                startOfToday,
                startOfTomorrow
        );

        Map<Long, List<Plan>> plansByStudyCategoryIds = todayPlans.stream()
                .collect(Collectors.groupingBy(Plan::getStudyCategoryId));

        List<DailyPlannerDto> dailyPlannerDtos = buildDailyPlannerDtos(studyCategories, plansByStudyCategoryIds);

        int completedPlanCount = countCompletedPlans(todayPlans);
        int totalPlanCount = todayPlans.size();

        return GetStudyMemberPlannerResponse.of(
                isMyPlanner,
                true,
                completedPlanCount,
                totalPlanCount,
                dailyPlannerDtos
        );
    }

    /**
     * 스터디 멤버의 플래너 공개 여부를 변경한다.
     * <p>
     * 본인에 대해서만 설정을 변경할 수 있으며, 다른 사용자의 플래너 공개 여부는 수정할 수 없다.
     * </p>
     *
     * @param request  플래너 공개 여부 변경 요청 DTO
     * @param studyId  대상 스터디 ID
     * @param memberId 설정을 변경할 멤버 사용자 ID
     * @param userId   변경을 요청한 사용자 ID
     * @throws UserAccessDeniedException 요청한 사용자가 본인이 아닌 경우
     * @throws UserNotFoundException     변경 대상 사용자가 존재하지 않는 경우
     */
    public void modifyPlannerVisibility(
            PatchPlannerVisibilityRequest request,
            Long studyId,
            Long memberId,
            Long userId
    ) {
        if (!memberId.equals(userId)) {
            throw new UserAccessDeniedException();
        }

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updatePlannerVisibility(request.isPlannerVisible());
    }

    /**
     * 사용자가 해당 스터디에 속해 있는지 검증한다.
     *
     * @param studyId 스터디 ID
     * @param userId  검증할 사용자 ID
     * @throws StudyAccessDeniedException 사용자가 스터디에 속해 있지 않은 경우
     */
    private void validateUserInStudy(Long studyId, Long userId) {
        if (!userStudyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new StudyAccessDeniedException();
        }
    }

    /**
     * 공부 시간(초)을 기준으로 학습 레벨을 결정한다.
     * <p>
     * 레벨 기준은 다음과 같다.
     * <ul>
     *     <li>0시간: 레벨 1</li>
     *     <li>2시간 미만: 레벨 2</li>
     *     <li>4시간 미만: 레벨 3</li>
     *     <li>6시간 미만: 레벨 4</li>
     *     <li>6시간 이상: 레벨 5</li>
     * </ul>
     * </p>
     *
     * @param seconds 공부 시간 (초)
     * @return 계산된 학습 레벨
     */
    private int determineLevelByStudyTime(Long seconds) {
        long hours = seconds / 3600;

        if (hours == 0) {
            return 1;
        }
        if (hours < 2) {
            return 2;
        }
        if (hours < 4) {
            return 3;
        }
        if (hours < 6) {
            return 4;
        }
        return 5;
    }

    /**
     * 월별 공부 시간 정보를 기반으로 월 단위 학습 DTO를 생성한다.
     * <p>
     * 해당 월의 일별 학습 기록을 기준으로 각 날짜별 학습 레벨 목록을 계산하여 DTO로 변환한다.
     * </p>
     *
     * @param yearMonth           월 정보
     * @param dailyStudySummaries 해당 월의 일일 학습 기록 목록
     * @return 월별 공부 시간 DTO
     */
    private MonthlyStudyTimeDto buildMonthlyStudyTimeDto(
            YearMonth yearMonth,
            List<DailyStudySummary> dailyStudySummaries
    ) {
        Map<Integer, DailyStudySummary> summaryByDay = groupByDayOfMonth(dailyStudySummaries);
        List<Integer> studyTimeLevels = calculateDailyStudyLevelsInMonth(yearMonth, summaryByDay);
        return MonthlyStudyTimeDto.of(yearMonth, studyTimeLevels);
    }

    /**
     * 지정한 월의 일별 학습 레벨 목록을 계산한다.
     * <p>
     * 해당 날짜에 학습 기록이 존재하지 않는 경우 레벨 0으로 처리한다.
     * </p>
     *
     * @param yearMonth    계산 대상 월
     * @param summaryByDay 일(day) 기준으로 그룹화된 학습 요약 정보
     * @return 날짜 순서대로 정렬된 일별 학습 레벨 목록
     */
    private List<Integer> calculateDailyStudyLevelsInMonth(
            YearMonth yearMonth,
            Map<Integer, DailyStudySummary> summaryByDay
    ) {
        return IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> {
                    DailyStudySummary dailyStudySummary = summaryByDay.get(day);
                    return (dailyStudySummary != null)
                            ? determineLevelByStudyTime(dailyStudySummary.getStudyTime())
                            : 0;
                })
                .toList();
    }

    /**
     * 일일 학습 기록을 날짜(day of month) 기준으로 그룹화한다.
     *
     * @param dailyStudySummaries 일일 학습 기록 목록
     * @return 날짜(day)를 키로 하는 학습 요약 정보 맵
     */
    private Map<Integer, DailyStudySummary> groupByDayOfMonth(List<DailyStudySummary> dailyStudySummaries) {
        return dailyStudySummaries.stream()
                .collect(Collectors.toMap(
                        dailyStudySummary -> dailyStudySummary.getDate().getDayOfMonth(),
                        dailyStudySummary -> dailyStudySummary
                ));
    }

    /**
     * 최근 {@code MONTH_RANGE}개월의 월별 학습 정보를 수집한다.
     * <p>
     * 시작 월부터 현재 월까지 순회하며, 각 월에 대한 {@link MonthlyStudyTimeDto}를 생성하여 {@code monthlyStudyTimeDtos}에 추가한다.
     * </p>
     * <p>
     * 현재 월에 대해서는 학습 기록이 존재하는 일수만을 집계하여 반환한다.
     * </p>
     *
     * @param summariesByMonth     월별로 그룹화된 일일 학습 요약 정보
     * @param monthlyStudyTimeDtos 월별 학습 DTO를 추가할 대상 리스트 (in-place 변경)
     * @return 현재 월의 학습 기록 일수
     */
    private int collectMonthlyStudyTimes(
            Map<YearMonth, List<DailyStudySummary>> summariesByMonth,
            List<MonthlyStudyTimeDto> monthlyStudyTimeDtos
    ) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(MONTH_RANGE - 1);

        int studyTimeCount = 0;

        for (int monthOffset = 0; monthOffset < MONTH_RANGE; monthOffset++) {
            YearMonth yearMonth = startMonth.plusMonths(monthOffset);

            List<DailyStudySummary> summaries =
                    summariesByMonth.getOrDefault(yearMonth, List.of());

            if (yearMonth.equals(currentMonth)) {
                studyTimeCount = summaries.size();
            }

            monthlyStudyTimeDtos.add(buildMonthlyStudyTimeDto(yearMonth, summaries));
        }

        return studyTimeCount;
    }

    /**
     * 완료된 플랜의 개수를 집계한다.
     *
     * @param planList 플랜 목록
     * @return 완료 상태인 플랜 수
     */
    private int countCompletedPlans(List<Plan> planList) {
        return (int) planList.stream()
                .filter(Plan::isCompleted)
                .count();
    }

    /**
     * 스터디 카테고리별 일일 플래너 DTO 목록을 생성한다.
     * <p>
     * 각 스터디 카테고리에 대해 해당 카테고리에 속한 플랜 목록을 매핑하여 {@link DailyPlannerDto}를 생성한다.
     * </p>
     * <p>
     * 특정 카테고리에 플랜이 존재하지 않는 경우, 빈 플랜 목록을 포함한 DTO를 생성한다.
     * </p>
     *
     * @param studyCategories         스터디 카테고리 목록
     * @param plansByStudyCategoryIds 카테고리 ID 기준으로 그룹화된 플랜 맵
     * @return 카테고리 순서를 유지한 일일 플래너 DTO 목록
     */
    private List<DailyPlannerDto> buildDailyPlannerDtos(
            List<StudyCategory> studyCategories,
            Map<Long, List<Plan>> plansByStudyCategoryIds
    ) {
        List<DailyPlannerDto> dailyPlannerDtos = new ArrayList<>();

        for (StudyCategory studyCategory : studyCategories) {
            List<Plan> plansOfStudyCategory = plansByStudyCategoryIds.getOrDefault(studyCategory.getId(), List.of());

            List<PlanDto> planDtos = plansOfStudyCategory.stream()
                    .map(PlanDto::from)
                    .toList();

            dailyPlannerDtos.add(DailyPlannerDto.of(studyCategory, planDtos));
        }

        return dailyPlannerDtos;
    }
}
