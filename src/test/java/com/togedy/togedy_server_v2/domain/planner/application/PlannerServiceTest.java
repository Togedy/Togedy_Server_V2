package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlannerDailyImageRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerShareResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerStatisticsResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTopResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyTimetableResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PutDailyPlannerImageRequest;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidPlannerImageException;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.global.service.S3Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlannerServiceTest {

    @Mock
    private UserScheduleRepository userScheduleRepository;

    @Mock
    private DailyStudySummaryRepository dailyStudySummaryRepository;

    @Mock
    private PlannerDailyImageRepository plannerDailyImageRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private StudyTaskService studyTaskService;

    @Mock
    private StudyTimeService studyTimeService;

    @InjectMocks
    private PlannerService plannerService;

    @Test
    void 일별_플래너_상단_조회_시_Dday가_있으면_Dday_정보를_반환한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 2);
        LocalDate ddayDate = queryDate.plusDays(8);

        UserSchedule schedule = UserSchedule.builder()
                .name("수능")
                .startDate(ddayDate)
                .dDay(true)
                .build();

        DailyStudySummary summary = DailyStudySummary.builder()
                .userId(userId)
                .date(queryDate)
                .studyTime(43204L)
                .build();

        given(userScheduleRepository.findByUserIdAndDDayTrue(any()))
                .willReturn(Optional.of(schedule));
        given(dailyStudySummaryRepository.findByUserIdAndDate(any(), any()))
                .willReturn(Optional.of(summary));
        given(plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(any(), any()))
                .willReturn(Optional.empty());

        GetDailyPlannerTopResponse response = plannerService.findDailyPlannerTop(queryDate, userId);

        assertThat(response.isHasDday()).isTrue();
        assertThat(response.getDate()).isEqualTo(queryDate);
        assertThat(response.getUserScheduleName()).isEqualTo("수능");
        assertThat(response.getRemainingDays()).isEqualTo((int) ChronoUnit.DAYS.between(queryDate, ddayDate));
        assertThat(response.getTotalStudyTime()).isEqualTo("12:00:04");
        assertThat(response.getPlannerImage()).isNull();
    }

    @Test
    void 일별_플래너_상단_조회_시_Dday가_없으면_관련_필드를_null로_반환한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 2);

        given(userScheduleRepository.findByUserIdAndDDayTrue(any()))
                .willReturn(Optional.empty());
        given(dailyStudySummaryRepository.findByUserIdAndDate(any(), any()))
                .willReturn(Optional.empty());
        given(plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(any(), any()))
                .willReturn(Optional.empty());

        GetDailyPlannerTopResponse response = plannerService.findDailyPlannerTop(queryDate, userId);

        assertThat(response.isHasDday()).isFalse();
        assertThat(response.getDate()).isEqualTo(queryDate);
        assertThat(response.getUserScheduleName()).isNull();
        assertThat(response.getRemainingDays()).isNull();
        assertThat(response.getTotalStudyTime()).isEqualTo("00:00:00");
        assertThat(response.getPlannerImage()).isNull();
    }

    @Test
    void 플래너_이미지_업서트_시_이미지와_remove가_모두_비정상인_경우_예외가_발생한다() {
        PutDailyPlannerImageRequest request = new PutDailyPlannerImageRequest(null, false);

        assertThatThrownBy(() -> plannerService.upsertDailyPlannerImage(LocalDate.of(2026, 3, 2), request, 1L))
                .isInstanceOf(InvalidPlannerImageException.class);
    }

    @Test
    void 일간_플래너_통계_조회_시_주간과_월간_복기_및_연속학습_정보를_반환한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 18);

        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 22)))
                .willReturn(List.of(
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 16)).studyTime(0L).build(),
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 17)).studyTime(0L).build(),
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 18)).studyTime(43260L).build()
                ));
        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)))
                .willReturn(List.of(
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 1)).studyTime(7201L).build(),
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 2)).studyTime(0L).build(),
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 3)).studyTime(18001L).build(),
                        DailyStudySummary.builder().userId(userId).date(LocalDate.of(2026, 3, 4)).studyTime(14401L).build()
                ));
        given(dailyStudySummaryRepository.findStudyDatesByUserIdUntilDateOrderByDateDesc(userId, queryDate))
                .willReturn(List.of(
                        LocalDate.of(2026, 3, 18),
                        LocalDate.of(2026, 3, 17),
                        LocalDate.of(2026, 3, 16),
                        LocalDate.of(2026, 3, 12)
                ));

        GetDailyPlannerStatisticsResponse response = plannerService.findDailyPlannerStatistics(queryDate, userId);

        assertThat(response.getDaysSinceLastStudy()).isEqualTo(0);
        assertThat(response.getCurrentStreakDays()).isEqualTo(3);
        assertThat(response.getWeeklyReview()).containsExactly(
                "00:00:00",
                "00:00:00",
                "12:01:00",
                null,
                null,
                null,
                null
        );
        assertThat(response.getMonthlyReview().subList(0, 4)).containsExactly(3, 1, 4, 4);
    }

    @Test
    void 일간_플래너_통계_조회_시_오늘_공부하지_않았으면_연속일수는_0이다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 18);

        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 22)))
                .willReturn(List.of());
        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(userId, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)))
                .willReturn(List.of());
        given(dailyStudySummaryRepository.findStudyDatesByUserIdUntilDateOrderByDateDesc(userId, queryDate))
                .willReturn(List.of(
                        LocalDate.of(2026, 3, 17),
                        LocalDate.of(2026, 3, 16),
                        LocalDate.of(2026, 3, 15)
                ));

        GetDailyPlannerStatisticsResponse response = plannerService.findDailyPlannerStatistics(queryDate, userId);

        assertThat(response.getDaysSinceLastStudy()).isEqualTo(1);
        assertThat(response.getCurrentStreakDays()).isEqualTo(0);
    }

    @Test
    void 일별_플래너_공유_조회_시_Dday가_없으면_nullable_필드들을_null로_반환한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 18);

        given(userScheduleRepository.findByUserIdAndDDayTrue(userId))
                .willReturn(Optional.empty());
        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, queryDate))
                .willReturn(Optional.empty());
        given(plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(userId, queryDate))
                .willReturn(Optional.empty());
        given(studyTaskService.findDailyPlannerShareItems(queryDate, userId))
                .willReturn(List.of());
        given(studyTimeService.findDailyTimetables(queryDate, userId))
                .willReturn(GetDailyTimetableResponse.of(List.of()));

        GetDailyPlannerShareResponse response = plannerService.findDailyPlannerShare(queryDate, userId);

        assertThat(response.isHasDday()).isFalse();
        assertThat(response.getUserScheduleName()).isNull();
        assertThat(response.getRemainingDays()).isNull();
        assertThat(response.getTotalStudyTime()).isEqualTo("00:00:00");
        assertThat(response.getPlannerItemList()).isEmpty();
        assertThat(response.getTimeTableList()).isEmpty();
    }

    @Test
    void 일별_플래너_공유_조회_시_Dday는_조회한_스터디데이_기준으로_계산한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 18);
        LocalDate ddayDate = queryDate.plusDays(5);

        UserSchedule schedule = UserSchedule.builder()
                .name("수능")
                .startDate(ddayDate)
                .dDay(true)
                .build();

        given(userScheduleRepository.findByUserIdAndDDayTrue(userId))
                .willReturn(Optional.of(schedule));
        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, queryDate))
                .willReturn(Optional.empty());
        given(plannerDailyImageRepository.findTopByUserIdAndDateLessThanEqualOrderByDateDesc(userId, queryDate))
                .willReturn(Optional.empty());
        given(studyTaskService.findDailyPlannerShareItems(queryDate, userId))
                .willReturn(List.of());
        given(studyTimeService.findDailyTimetables(queryDate, userId))
                .willReturn(GetDailyTimetableResponse.of(List.of()));

        GetDailyPlannerShareResponse response = plannerService.findDailyPlannerShare(queryDate, userId);

        assertThat(response.getDate()).isEqualTo(queryDate);
        assertThat(response.getRemainingDays()).isEqualTo((int) ChronoUnit.DAYS.between(queryDate, ddayDate));
    }
}
