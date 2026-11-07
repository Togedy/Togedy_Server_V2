package com.togedy.togedy_server_v2.domain.study.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyStatisticsRepository;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.StudyStatistics;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTier;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.support.AbstractE2ETest;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class StudyTierServiceTest extends AbstractE2ETest {

    private static final long SCORE_BELOW_BRONZE2 = 49_00000000L;

    @Autowired
    StudyTierService studyTierService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyStatisticsRepository studyStatisticsRepository;

    @Autowired
    DailyStudySummaryRepository dailyStudySummaryRepository;

    @Test
    @DisplayName("챌린지 목표를 달성하면 점수 정산 후 갱신된 점수로 스터디 티어가 갱신된다.")
    void applyStudyTierAfterChallengeSuccess() {
        //given
        LocalDate yesterday = TimeUtil.todayInStudyZone().minusDays(1);
        Study study = persistChallengeStudyWithLeader(3 * 3600L, yesterday);
        persistStatistics(study, SCORE_BELOW_BRONZE2, 0, yesterday.minusDays(1));

        //when
        studyTierService.calculateChallengeStudyScores();
        studyTierService.applyStudyTier();

        //then
        StudyStatistics statistics = studyStatisticsRepository.findByStudyId(study.getId()).orElseThrow();
        assertThat(statistics.getUpdatedDate()).isEqualTo(yesterday);
        assertThat(statistics.getStreakDays()).isEqualTo(1);
        assertThat(statistics.getScore()).isGreaterThan(SCORE_BELOW_BRONZE2);

        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertThat(updatedStudy.getTier()).isEqualTo(StudyTier.BRONZE2);
    }

    @Test
    @DisplayName("챌린지 목표를 달성하지 못하면 연속 달성 일수가 초기화되고 티어는 유지된다.")
    void resetStreakAndKeepTierAfterChallengeFailure() {
        //given
        LocalDate yesterday = TimeUtil.todayInStudyZone().minusDays(1);
        Study study = persistChallengeStudyWithLeader(3600L, yesterday);
        persistStatistics(study, SCORE_BELOW_BRONZE2, 3, yesterday.minusDays(1));

        //when
        studyTierService.calculateChallengeStudyScores();
        studyTierService.applyStudyTier();

        //then
        StudyStatistics statistics = studyStatisticsRepository.findByStudyId(study.getId()).orElseThrow();
        assertThat(statistics.getUpdatedDate()).isEqualTo(yesterday);
        assertThat(statistics.getStreakDays()).isZero();
        assertThat(statistics.getScore()).isEqualTo(SCORE_BELOW_BRONZE2);

        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertThat(updatedStudy.getTier()).isEqualTo(StudyTier.BRONZE1);
    }

    private Study persistChallengeStudyWithLeader(long studyTime, LocalDate date) {
        User user = fixtureSupport.persistUser(UserFixture.createUser());
        Study study = fixtureSupport.persistStudy(StudyFixture.createChallengeStudy());
        fixtureSupport.persistUserStudy(study, user, StudyRole.LEADER);

        dailyStudySummaryRepository.save(DailyStudySummary.builder()
                .userId(user.getId())
                .studyTime(studyTime)
                .date(date)
                .build());

        return study;
    }

    private void persistStatistics(Study study, long score, int streakDays, LocalDate updatedDate) {
        StudyStatistics statistics = StudyStatistics.builder()
                .studyId(study.getId())
                .updatedDate(updatedDate)
                .build();
        ReflectionTestUtils.setField(statistics, "score", score);
        ReflectionTestUtils.setField(statistics, "streakDays", streakDays);
        studyStatisticsRepository.save(statistics);
    }
}
