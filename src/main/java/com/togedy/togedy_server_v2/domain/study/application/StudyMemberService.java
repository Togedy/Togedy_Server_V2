package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlanRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.Plan;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import com.togedy.togedy_server_v2.domain.planner.enums.PlanStatus;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.DailyPlannerDto;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyAttendanceResponse;
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
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public GetStudyMemberProfileResponse findStudyMemberProfile(Long studyId, Long memberId, Long userId) {
        validateUserInStudy(studyId, userId);

        User member = userRepository.findById(memberId).orElseThrow(UserNotFoundException::new);

        Long totalStudyTime = dailyStudySummaryRepository.findTotalStudyTimeByUserId(memberId).orElse(0L);

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        return GetStudyMemberProfileResponse.of(member, TimeUtil.toTimeFormat(totalStudyTime),
                userStudy.calculateElapsedDays());
    }

    public GetStudyMemberStudyTimeResponse findStudyMemberStudyTime(Long studyId, Long memberId, Long userId) {
        validateUserInStudy(studyId, userId);

        LocalDateTime start = TimeUtil.startOfMonthsAgo(5);
        LocalDateTime end = TimeUtil.startOfNextMonth();

        List<DailyStudySummary> dailyStudySummaries =
                dailyStudySummaryRepository.findAllByUserIdAndPeriod(memberId, start, end);

        Map<YearMonth, List<DailyStudySummary>> summariesByMonth = dailyStudySummaries.stream()
                .collect(Collectors.groupingBy(dss -> YearMonth.from(dss.getCreatedAt())));

        List<MonthlyStudyTimeDto> monthlyStudyTimeDtoList = new ArrayList<>();

        int studyTimeCount = collectMonthlyStudyTime(summariesByMonth, monthlyStudyTimeDtoList);

        return GetStudyMemberStudyTimeResponse.of(studyTimeCount, monthlyStudyTimeDtoList);
    }

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

    public List<GetStudyAttendanceResponse> findStudyAttendance(LocalDate startDate, LocalDate endDate, Long studyId) {
        List<User> userList = userRepository.findAllByStudyId(studyId);
        List<Long> userIds = userList.stream().map(User::getId).toList();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<DailyStudySummary> dailyStudySummaryList = dailyStudySummaryRepository.findAllByUserIdsAndPeriod(userIds,
                startDateTime, endDateTime);

        Map<Long, List<DailyStudySummary>> dailyStudySummaryMap = dailyStudySummaryList.stream()
                .collect(Collectors.groupingBy(DailyStudySummary::getUserId));

        List<Object[]> dailyStudyTimeList = dailyStudySummaryRepository.findTotalStudyTimeByUserIdsAndPeriod(userIds,
                startDateTime, endDateTime);

        Map<Long, Long> totalStudyTimeMap = new HashMap<>(dailyStudyTimeList.stream().collect(
                Collectors.toMap(row -> ((Number) row[0]).longValue(),
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L, Long::sum)));

        List<GetStudyAttendanceResponse> response = userList.stream().map(user -> {
                    List<String> studyTimeList = new ArrayList<>();
                    for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                        LocalDateTime dayStart = d.atStartOfDay();
                        LocalDateTime dayEnd = d.atTime(LocalTime.MAX);

                        long totalStudyTime = dailyStudySummaryMap.getOrDefault(user.getId(), List.of()).stream()
                                .filter(s -> !s.getCreatedAt().isBefore(dayStart) && !s.getCreatedAt().isAfter(dayEnd))
                                .mapToLong(DailyStudySummary::getStudyTime).sum();

                        studyTimeList.add(
                                Optional.of(totalStudyTime).filter(s -> s > 0L).map(TimeUtil::toTimeFormat).orElse(null));
                    }

                    return GetStudyAttendanceResponse.of(user, studyTimeList);
                }).sorted(Comparator.comparingLong(
                        (GetStudyAttendanceResponse dto) -> totalStudyTimeMap.getOrDefault(dto.getUserId(), 0L)).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        return response;
    }

    public void modifyPlannerVisibility(PatchPlannerVisibilityRequest request, Long studyId, Long memberId,
                                        Long userId) {
        if (!memberId.equals(userId)) {
            throw new UserAccessDeniedException();
        }

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updatePlannerVisibility(request.isPlannerVisible());
    }

    /**
     * 해당 유저가 스터디에 속해 있는지(참여 중인지) 검증한다.
     *
     * @param studyId 스터디 ID
     * @param userId  유저 ID
     */
    private void validateUserInStudy(Long studyId, Long userId) {
        if (!userStudyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new StudyAccessDeniedException();
        }
    }

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

    private MonthlyStudyTimeDto buildMonthlyStudyTimeDto(
            YearMonth yearMonth,
            List<DailyStudySummary> dailyStudySummaries
    ) {
        Map<Integer, DailyStudySummary> summaryByDay = groupByDay(dailyStudySummaries);
        List<Integer> studyTimeLevels = calculateDailyStudyLevels(yearMonth, summaryByDay);
        return MonthlyStudyTimeDto.of(yearMonth, studyTimeLevels);
    }

    private List<Integer> calculateDailyStudyLevels(YearMonth yearMonth, Map<Integer, DailyStudySummary> summaryByDay) {
        return IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> {
                    DailyStudySummary dailyStudySummary = summaryByDay.get(day);
                    return (dailyStudySummary != null)
                            ? determineLevelByStudyTime(dailyStudySummary.getStudyTime())
                            : 0;
                })
                .toList();
    }

    private Map<Integer, DailyStudySummary> groupByDay(List<DailyStudySummary> dailyStudySummaries) {
        return dailyStudySummaries.stream()
                .collect(Collectors.toMap(
                        dailyStudySummary -> dailyStudySummary.getCreatedAt().getDayOfMonth(),
                        dailyStudySummary -> dailyStudySummary
                ));
    }

    private int collectMonthlyStudyTime(
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

    private int countCompletedPlans(List<Plan> planList) {
        return (int) planList.stream()
                .filter(plan -> plan.getStatus() == PlanStatus.SUCCESS)
                .count();
    }

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
