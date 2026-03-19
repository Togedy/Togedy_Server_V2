package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlannerDailyImageRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerShareResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerStatisticsResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTopResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetMonthlyPlannerHeatmapResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PutDailyPlannerImageRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.PlannerDailyImage;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidPlannerImageException;
import com.togedy.togedy_server_v2.global.enums.ImageCategory;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import java.time.Duration;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlannerService {

    private final UserScheduleRepository userScheduleRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final PlannerDailyImageRepository plannerDailyImageRepository;
    private final S3Service s3Service;
    private final StudyTaskService studyTaskService;
    private final StudyTimeService studyTimeService;

    @Transactional(readOnly = true)
    public GetDailyPlannerTopResponse findDailyPlannerTop(LocalDate date, Long userId) {
        PlannerDailyContext context = getPlannerDailyContext(date, userId);
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        if (dDaySchedule.isPresent()) {
            UserSchedule schedule = dDaySchedule.get();
            int remainingDays = TimeUtil.calculateDaysUntil(schedule.getStartDate());

            return GetDailyPlannerTopResponse.of(
                    context.studyDate(),
                    true,
                    schedule.getName(),
                    remainingDays,
                    TimeUtil.formatSecondsToHms(context.dailyStudyTime()),
                    context.plannerImage()
            );
        }

        return GetDailyPlannerTopResponse.of(
                context.studyDate(),
                false,
                null,
                null,
                TimeUtil.formatSecondsToHms(context.dailyStudyTime()),
                context.plannerImage()
        );
    }

    @Transactional(readOnly = true)
    public GetMonthlyPlannerHeatmapResponse findMonthlyPlannerHeatmap(YearMonth month, Long userId) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<DailyStudySummary> dailyStudySummaries = dailyStudySummaryRepository.findAllByUserIdAndPeriod(
                userId,
                startDate,
                endDate
        );

        return GetMonthlyPlannerHeatmapResponse.of(buildMonthlyReview(startDate, endDate, dailyStudySummaries));
    }

    @Transactional(readOnly = true)
    public GetDailyPlannerShareResponse findDailyPlannerShare(LocalDate date, Long userId) {
        PlannerDailyContext context = getPlannerDailyContext(date, userId);
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);

        return GetDailyPlannerShareResponse.of(
                context.studyDate(),
                dDaySchedule.isPresent(),
                dDaySchedule.map(UserSchedule::getName).orElse(null),
                dDaySchedule.map(schedule -> TimeUtil.calculateDaysUntil(schedule.getStartDate())).orElse(null),
                TimeUtil.formatSecondsToHms(context.dailyStudyTime()),
                context.plannerImage(),
                studyTaskService.findDailyPlannerShareItems(context.studyDate(), userId),
                studyTimeService.findDailyTimetables(context.studyDate(), userId).getTimeTableList()
        );
    }

    @Transactional(readOnly = true)
    public GetDailyPlannerStatisticsResponse findDailyPlannerStatistics(LocalDate date, Long userId) {
        LocalDate studyDate = TimeUtil.resolveStudyDate(date);
        LocalDate weekStart = studyDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate monthStart = YearMonth.from(studyDate).atDay(1);
        LocalDate monthEnd = YearMonth.from(studyDate).atEndOfMonth();

        List<DailyStudySummary> weeklySummaries = dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, weekStart, weekEnd);
        List<DailyStudySummary> monthlySummaries = dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, monthStart, monthEnd);
        List<LocalDate> studyDates = dailyStudySummaryRepository.findStudyDatesByUserIdUntilDateOrderByDateDesc(userId, studyDate);

        return GetDailyPlannerStatisticsResponse.of(
                calculateDaysSinceLastStudy(studyDate, studyDates),
                calculateCurrentStreakDays(studyDate, studyDates),
                buildWeeklyReview(weekStart, studyDate, weeklySummaries),
                buildMonthlyReview(monthStart, monthEnd, monthlySummaries)
        );
    }

    @Transactional
    public void upsertDailyPlannerImage(LocalDate date, PutDailyPlannerImageRequest request, Long userId) {
        LocalDate studyDate = TimeUtil.resolveStudyDate(date);
        String plannerImageUrl = resolvePlannerImageUrl(request);
        PlannerDailyImage dailyImage = plannerDailyImageRepository.findByUserIdAndDate(userId, studyDate)
                .orElseGet(() -> PlannerDailyImage.builder()
                        .userId(userId)
                        .date(studyDate)
                        .build());

        dailyImage.updateImageUrl(plannerImageUrl);
        plannerDailyImageRepository.save(dailyImage);
    }

    private String resolvePlannerImageUrl(PutDailyPlannerImageRequest request) {
        if (request.isRemovePlannerImage()) {
            return null;
        }

        if (request.getPlannerImage() == null || request.getPlannerImage().isEmpty()) {
            throw new InvalidPlannerImageException();
        }

        return s3Service.uploadFile(request.getPlannerImage(), ImageCategory.PLANNER);
    }

    private int toHeatmapLevel(long studyTimeSeconds) {
        if (studyTimeSeconds <= 0) {
            return 1;
        }
        if (studyTimeSeconds <= Duration.ofHours(2).toSeconds()) {
            return 2;
        }
        if (studyTimeSeconds <= Duration.ofHours(4).toSeconds()) {
            return 3;
        }
        if (studyTimeSeconds <= Duration.ofHours(6).toSeconds()) {
            return 4;
        }
        return 5;
    }

    private List<String> buildWeeklyReview(LocalDate weekStart, LocalDate studyDate, List<DailyStudySummary> weeklySummaries) {
        Map<LocalDate, Long> studyTimeByDate = mapStudyTimeByDate(weeklySummaries);

        return weekStart.datesUntil(weekStart.plusDays(7))
                .map(date -> {
                    if (date.isAfter(studyDate)) {
                        return null;
                    }
                    return TimeUtil.formatSecondsToHms(studyTimeByDate.getOrDefault(date, 0L));
                })
                .toList();
    }

    private List<Integer> buildMonthlyReview(LocalDate monthStart, LocalDate monthEnd, List<DailyStudySummary> monthlySummaries) {
        Map<LocalDate, Long> studyTimeByDate = mapStudyTimeByDate(monthlySummaries);

        return monthStart.datesUntil(monthEnd.plusDays(1))
                .map(date -> toHeatmapLevel(studyTimeByDate.getOrDefault(date, 0L)))
                .toList();
    }

    private Map<LocalDate, Long> mapStudyTimeByDate(List<DailyStudySummary> summaries) {
        Map<LocalDate, Long> studyTimeByDate = new HashMap<>();
        for (DailyStudySummary summary : summaries) {
            studyTimeByDate.put(summary.getDate(), summary.getStudyTime());
        }
        return studyTimeByDate;
    }

    private PlannerDailyContext getPlannerDailyContext(LocalDate date, Long userId) {
        LocalDate studyDate = TimeUtil.resolveStudyDate(date);
        Long dailyStudyTime = dailyStudySummaryRepository.findByUserIdAndDate(userId, studyDate)
                .map(DailyStudySummary::getStudyTime)
                .orElse(0L);
        String plannerImage = plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(userId, studyDate)
                .map(PlannerDailyImage::getImageUrl)
                .orElse(null);
        return new PlannerDailyContext(studyDate, dailyStudyTime, plannerImage);
    }

    private int calculateDaysSinceLastStudy(LocalDate studyDate, List<LocalDate> studyDates) {
        if (studyDates.isEmpty()) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(studyDates.get(0), studyDate);
    }

    private int calculateCurrentStreakDays(LocalDate studyDate, List<LocalDate> studyDates) {
        if (studyDates.isEmpty() || !studyDates.get(0).equals(studyDate)) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = studyDate;
        for (LocalDate studyRecordDate : studyDates) {
            if (!studyRecordDate.equals(expectedDate)) {
                break;
            }
            streak++;
            expectedDate = expectedDate.minusDays(1);
        }
        return streak;
    }

    private record PlannerDailyContext(LocalDate studyDate, Long dailyStudyTime, String plannerImage) {
    }
}
