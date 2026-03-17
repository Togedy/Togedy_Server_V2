package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlannerDailyImageRepository;
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
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.YearMonth;
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

    @Transactional(readOnly = true)
    public GetDailyPlannerTopResponse findDailyPlannerTop(LocalDate date, Long userId) {
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);
        Long dailyStudyTime = dailyStudySummaryRepository.findByUserIdAndDate(userId, date)
                .map(DailyStudySummary::getStudyTime)
                .orElse(0L);
        String plannerImage = plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(userId, date)
                .map(PlannerDailyImage::getImageUrl)
                .orElse(null);

        if (dDaySchedule.isPresent()) {
            UserSchedule schedule = dDaySchedule.get();
            int remainingDays = TimeUtil.calculateDaysUntil(schedule.getStartDate());

            return GetDailyPlannerTopResponse.of(
                    date,
                    true,
                    schedule.getName(),
                    remainingDays,
                    dailyStudyTime,
                    plannerImage
            );
        }

        return GetDailyPlannerTopResponse.of(
                date,
                false,
                null,
                null,
                dailyStudyTime,
                plannerImage
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

        Map<LocalDate, Long> studyTimeByDate = new HashMap<>();
        for (DailyStudySummary summary : dailyStudySummaries) {
            studyTimeByDate.put(summary.getDate(), summary.getStudyTime());
        }

        List<Integer> heatmapList = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> toHeatmapLevel(studyTimeByDate.getOrDefault(date, 0L)))
                .toList();

        return GetMonthlyPlannerHeatmapResponse.of(heatmapList);
    }

    @Transactional
    public void upsertDailyPlannerImage(LocalDate date, PutDailyPlannerImageRequest request, Long userId) {
        String plannerImageUrl = resolvePlannerImageUrl(request);
        PlannerDailyImage dailyImage = plannerDailyImageRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> PlannerDailyImage.builder()
                        .userId(userId)
                        .date(date)
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
        if (studyTimeSeconds <= 2 * 60 * 60) {
            return 2;
        }
        if (studyTimeSeconds <= 4 * 60 * 60) {
            return 3;
        }
        if (studyTimeSeconds <= 6 * 60 * 60) {
            return 4;
        }
        return 5;
    }
}
