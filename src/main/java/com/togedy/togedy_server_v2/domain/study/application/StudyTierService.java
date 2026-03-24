package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyStatisticsRepository;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudySummaryRow;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.StudyStatistics;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyTierService {

    private final StudyStatisticsRepository studyStatisticsRepository;
    private final StudyRepository studyRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional
    public void aggregateRunningStudyTimes() {
        LocalDateTime summaryEnd = TimeUtil.startOfStudyDay(LocalDateTime.now());
        LocalDateTime summaryStart = summaryEnd.minusDays(1);
        LocalDate targetDate = summaryStart.toLocalDate();

        List<StudyTime> runningStudyTimes = studyTimeRepository.findRunningStudyTimesBefore(summaryEnd);

        for (StudyTime studyTime : runningStudyTimes) {
            long studySeconds = calculatePreviousDayStudySeconds(studyTime, summaryStart, summaryEnd);

            if (studySeconds == 0) {
                continue;
            }

            DailyStudySummary dailyStudySummary = dailyStudySummaryRepository.findByUserIdAndDate(
                            studyTime.getUserId(),
                            targetDate
                    )
                    .orElseGet(() -> DailyStudySummary.builder()
                            .userId(studyTime.getUserId())
                            .studyTime(0L)
                            .date(targetDate)
                            .build());

            dailyStudySummary.addStudyTime(studySeconds);
            dailyStudySummaryRepository.save(dailyStudySummary);
        }
    }

    @Transactional
    public void calculateChallengeStudyScores() {
        LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        List<Study> challengeStudies = studyRepository.findChallengeStudy();
        List<Long> studyIds = challengeStudies.stream()
                .map(Study::getId)
                .toList();

        List<DailyStudySummaryRow> dailyStudySummaryRows =
                dailyStudySummaryRepository.findAllByStudyIdsAndDate(studyIds, targetDate);

        Map<Long, List<DailyStudySummaryRow>> dailyStudySummaryMap = dailyStudySummaryRows.stream()
                .collect(Collectors.groupingBy(DailyStudySummaryRow::getStudyId));

        for (Study challengeStudy : challengeStudies) {
            List<DailyStudySummaryRow> rows = dailyStudySummaryMap.get(challengeStudy.getId());

            int completedMembers = countCompletedMembers(challengeStudy, rows);

            StudyStatistics studyStatistics = studyStatisticsRepository.findByStudyId(challengeStudy.getId());
            studyStatistics.applyChallengeSuccess(challengeStudy, completedMembers);
            studyStatisticsRepository.save(studyStatistics);
        }
    }

    @Transactional
    public void applyStudyTier() {
        List<StudyStatistics> statistics = studyStatisticsRepository.findUpdatedToday(LocalDate.now());

        if (statistics.isEmpty()) {
            return;
        }

        Map<Long, StudyStatistics> statisticsMap = statistics.stream()
                .collect(Collectors.toMap(
                        StudyStatistics::getStudyId,
                        Function.identity()
                ));

        List<Study> studies = studyRepository.findAllByIds(statisticsMap.keySet());

        for (Study study : studies) {
            StudyStatistics studyStatistics = statisticsMap.get(study.getId());
            study.updateTier(studyStatistics);
        }
    }

    private int countCompletedMembers(Study study, List<DailyStudySummaryRow> rows) {
        return (int) rows.stream()
                .filter(dailyStudySummaryRow ->
                        study.isChallengeAchievedBy(dailyStudySummaryRow.getStudyTime()))
                .count();
    }

    private long calculatePreviousDayStudySeconds(
            StudyTime studyTime,
            LocalDateTime summaryStart,
            LocalDateTime summaryEnd
    ) {
        LocalDateTime effectiveStart = studyTime.getStartTime().isAfter(summaryStart)
                ? studyTime.getStartTime()
                : summaryStart;

        if (!effectiveStart.isBefore(summaryEnd)) {
            return 0L;
        }

        return Duration.between(effectiveStart, summaryEnd).getSeconds();
    }
}
