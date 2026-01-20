package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyStatisticsRepository;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudySummaryRow;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.StudyStatistics;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
        List<StudyStatistics> statistics =
                studyStatisticsRepository.findUpdatedToday(LocalDate.now());

        if (statistics.isEmpty()) {
            return;
        }

        Map<Long, StudyStatistics> statisticsMap =
                statistics.stream()
                        .collect(Collectors.toMap(
                                StudyStatistics::getStudyId,
                                Function.identity()
                        ));

        List<Study> studies =
                studyRepository.findAllByIds(statisticsMap.keySet());

        for (Study study : studies) {
            StudyStatistics studyStatistics = statisticsMap.get(study.getId());
            study.updateTier(studyStatistics);
        }
    }

    private int countCompletedMembers(Study study, List<DailyStudySummaryRow> rows) {
        return (int) rows.stream()
                .filter(dailyStudySummaryRow ->
                        study.isChallengeAchievedBy(dailyStudySummaryRow.getStudyTime())
                ).count();
    }
}
