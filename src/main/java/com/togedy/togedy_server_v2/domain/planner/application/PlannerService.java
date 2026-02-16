package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTopResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlannerService {

    private final UserScheduleRepository userScheduleRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;

    public GetDailyPlannerTopResponse findDailyPlannerTop(LocalDate date, Long userId) {
        Optional<UserSchedule> dDaySchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);
        Long dailyStudyTime = dailyStudySummaryRepository.findByUserIdAndDate(userId, date)
                .map(DailyStudySummary::getStudyTime)
                .orElse(0L);

        if (dDaySchedule.isPresent()) {
            UserSchedule schedule = dDaySchedule.get();
            int remainingDays = TimeUtil.calculateDaysUntil(schedule.getStartDate());

            return GetDailyPlannerTopResponse.of(
                    date,
                    true,
                    schedule.getName(),
                    remainingDays,
                    TimeUtil.formatSecondsToHms(dailyStudyTime),
                    null
            );
        }

        return GetDailyPlannerTopResponse.of(
                date,
                false,
                null,
                null,
                TimeUtil.formatSecondsToHms(dailyStudyTime),
                null
        );
    }
}
