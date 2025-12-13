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
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public GetStudyMemberProfileResponse findStudyMemberProfile(Long studyId, Long memberId, Long userId) {
        validateStudyMember(studyId, userId);

        User member = userRepository.findById(memberId).orElseThrow(UserNotFoundException::new);
        Long totalStudyTime = dailyStudySummaryRepository.findTotalStudyTimeByUserId(memberId).orElse(0L);
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);
        int elapsedDays = userStudy.calculateElapsedDays();

        return GetStudyMemberProfileResponse.of(
                member,
                TimeUtil.toTimeFormat(totalStudyTime),
                elapsedDays
        );
    }

    public GetStudyMemberStudyTimeResponse findStudyMemberStudyTime(Long studyId, Long memberId, Long userId) {
        validateStudyMember(studyId, userId);

        LocalDateTime startDateTime = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();

        List<DailyStudySummary> grouped =
                dailyStudySummaryRepository.findAllByUserIdAndPeriod(memberId, startDateTime, endDateTime);

        Map<YearMonth, List<DailyStudySummary>> monthListMap = grouped.stream()
                .collect(Collectors.groupingBy(dailyStudySummary -> YearMonth.from(dailyStudySummary.getCreatedAt())));

        List<MonthlyStudyTimeDto> monthlyStudyTimeDtoList = new ArrayList<>();
        int studyTimeCount = 0;

        for (Map.Entry<YearMonth, List<DailyStudySummary>> entry : monthListMap.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            List<DailyStudySummary> dailyStudySummaryList = entry.getValue();

            monthlyStudyTimeDtoList.add(
                    toMonthlyStudyTimeDto(yearMonth.getYear(), yearMonth.getMonthValue(), dailyStudySummaryList));

            if (yearMonth.equals(YearMonth.now())) {
                studyTimeCount = dailyStudySummaryList.size();
            }
        }

        return GetStudyMemberStudyTimeResponse.of(studyTimeCount, monthlyStudyTimeDtoList);
    }

    public GetStudyMemberPlannerResponse findStudyMemberPlanner(Long studyId, Long memberId, Long userId) {
        validateStudyMember(studyId, userId);

        User member = userRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);

        boolean isMyPlanner = member.getId().equals(userId);
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        int completedPlanCount = 0;
        int totalPlanCount = 0;

        List<DailyPlannerDto> dailyPlannerDtoList = new ArrayList<>();

        if (member.isPlannerVisible()) {
            List<StudyCategory> studyCategoryList = studyCategoryRepository.findAllByUserId(memberId);

            for (StudyCategory studyCategory : studyCategoryList) {
                List<Plan> planList =
                        planRepository.findByStudyCategoryIdAndCreatedAtBetween(studyCategory.getId(), startOfDay,
                                endOfDay);

                totalPlanCount += planList.size();
                completedPlanCount += (int) planList.stream()
                        .filter(plan -> plan.getStatus() == PlanStatus.SUCCESS)
                        .count();

                List<PlanDto> planDtoList = planList.stream()
                        .map(PlanDto::from)
                        .toList();

                dailyPlannerDtoList.add(DailyPlannerDto.of(studyCategory, planDtoList));
            }

            return GetStudyMemberPlannerResponse.of(
                    isMyPlanner,
                    true,
                    completedPlanCount,
                    totalPlanCount,
                    dailyPlannerDtoList
            );
        }

        return GetStudyMemberPlannerResponse.of(isMyPlanner, false);
    }

    public List<GetStudyMemberManagementResponse> findStudyMemberManagement(Long studyId, Long userId) {
        validateStudyMember(studyId, userId);

        List<GetStudyMemberManagementResponse> responses = userStudyRepository.findStudyMembersByStudyId(
                studyId);

        responses.stream()
                .filter(response -> Objects.equals(response.getUserId(), userId))
                .findFirst()
                .ifPresent(currentUser -> {
                    responses.remove(currentUser);
                    responses.add(0, currentUser);
                });

        return responses;
    }

    public List<GetStudyAttendanceResponse> findStudyAttendance(LocalDate startDate, LocalDate endDate, Long studyId) {
        List<User> userList = userRepository.findAllByStudyId(studyId);
        List<Long> userIds = userList.stream().map(User::getId).toList();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<DailyStudySummary> dailyStudySummaryList = dailyStudySummaryRepository
                .findAllByUserIdsAndPeriod(userIds, startDateTime, endDateTime);

        Map<Long, List<DailyStudySummary>> dailyStudySummaryMap = dailyStudySummaryList.stream()
                .collect(Collectors.groupingBy(DailyStudySummary::getUserId));

        List<Object[]> dailyStudyTimeList = dailyStudySummaryRepository
                .findTotalStudyTimeByUserIdsAndPeriod(userIds, startDateTime, endDateTime);

        Map<Long, Long> totalStudyTimeMap = new HashMap<>(
                dailyStudyTimeList.stream()
                        .collect(Collectors.toMap(
                                row -> ((Number) row[0]).longValue(),
                                row -> row[1] != null ? ((Number) row[1]).longValue() : 0L,
                                Long::sum
                        ))
        );

        List<GetStudyAttendanceResponse> response = userList.stream()
                .map(user -> {
                    List<String> studyTimeList = new ArrayList<>();
                    for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                        LocalDateTime dayStart = d.atStartOfDay();
                        LocalDateTime dayEnd = d.atTime(LocalTime.MAX);

                        long totalStudyTime = dailyStudySummaryMap
                                .getOrDefault(user.getId(), List.of())
                                .stream()
                                .filter(s -> !s.getCreatedAt().isBefore(dayStart) &&
                                        !s.getCreatedAt().isAfter(dayEnd))
                                .mapToLong(DailyStudySummary::getStudyTime)
                                .sum();

                        studyTimeList.add(
                                Optional.of(totalStudyTime)
                                        .filter(s -> s > 0L)
                                        .map(TimeUtil::toTimeFormat)
                                        .orElse(null)
                        );
                    }

                    return GetStudyAttendanceResponse.of(user, studyTimeList);
                }).sorted(Comparator.comparingLong(
                        (GetStudyAttendanceResponse dto) -> totalStudyTimeMap.getOrDefault(dto.getUserId(), 0L)
                ).reversed()).collect(Collectors.toCollection(ArrayList::new));

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
     * 스터디에 해당 유저의 존재 여부를 검증한다.
     *
     * @param studyId 스터디 ID
     * @param userId  유저 ID
     */
    private void validateStudyMember(Long studyId, Long userId) {
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

    private MonthlyStudyTimeDto toMonthlyStudyTimeDto(int year, int month, List<DailyStudySummary> summaries) {
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();

        Map<Integer, DailyStudySummary> byDay = summaries.stream()
                .collect(Collectors.toMap(
                        dss -> dss.getCreatedAt().getDayOfMonth(),
                        dss -> dss
                ));

        List<Integer> studyTimeLevels = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            DailyStudySummary dss = byDay.get(day);
            if (dss != null) {
                studyTimeLevels.add(determineLevelByStudyTime(dss.getStudyTime()));
            } else {
                studyTimeLevels.add(0);
            }
        }

        return MonthlyStudyTimeDto.of(year, month, studyTimeLevels);
    }
}
