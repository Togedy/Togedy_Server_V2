package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlannerDailyImageRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTopResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
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

    @InjectMocks
    private PlannerService plannerService;

    @Test
    void 일별_플래너_상단_조회_시_Dday가_있으면_Dday_정보를_반환한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 3, 2);
        LocalDate ddayDate = LocalDate.now().plusDays(8);

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
        assertThat(response.getRemainingDays()).isEqualTo(TimeUtil.calculateDaysUntil(ddayDate));
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
}
