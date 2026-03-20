package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetRunningTimerResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetTimerTotalResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.SubjectStudyTimeItemResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidStudySubjectException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyRunningException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyStoppedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerNotOwnedException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TimerServiceTest {

    @Mock
    private DailyStudySummaryRepository dailyStudySummaryRepository;

    @Mock
    private StudySubjectRepository studySubjectRepository;

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @InjectMocks
    private TimerService timerService;

    @Test
    void 타이머를_시작한다() {
        Long userId = 1L;
        Long studySubjectId = 10L;

        PostTimerStartRequest request = new PostTimerStartRequest();
        ReflectionTestUtils.setField(request, "studySubjectId", studySubjectId);

        StudySubject studySubject = StudySubject.builder()
                .userId(userId)
                .name("수학")
                .color("파란색")
                .orderIndex(1L)
                .build();
        ReflectionTestUtils.setField(studySubject, "id", studySubjectId);

        StudyTime saved = StudyTime.builder()
                .userId(userId)
                .studySubjectId(studySubjectId)
                .build();
        ReflectionTestUtils.setField(saved, "id", 100L);

        given(studyTimeRepository.findByUserIdAndEndTimeIsNull(userId)).willReturn(Optional.empty());
        given(studySubjectRepository.findActiveById(studySubjectId)).willReturn(Optional.of(studySubject));
        given(studyTimeRepository.saveAndFlush(any(StudyTime.class))).willReturn(saved);

        PostTimerStartResponse response = timerService.startTimer(request, userId);

        assertThat(response.getTimerId()).isEqualTo(100L);
        assertThat(response.getStartTime()).isNotNull();
    }

    @Test
    void 진행_중인_타이머가_있으면_예외가_발생한다() {
        Long userId = 1L;

        PostTimerStartRequest request = new PostTimerStartRequest();
        ReflectionTestUtils.setField(request, "studySubjectId", 10L);

        StudyTime running = StudyTime.builder()
                .userId(userId)
                .studySubjectId(10L)
                .build();

        given(studyTimeRepository.findByUserIdAndEndTimeIsNull(userId)).willReturn(Optional.of(running));

        assertThatThrownBy(() -> timerService.startTimer(request, userId))
                .isInstanceOf(TimerAlreadyRunningException.class);
    }

    @Test
    void 과목_id가_없거나_유효하지_않으면_예외가_발생한다() {
        Long userId = 1L;
        PostTimerStartRequest request = new PostTimerStartRequest();

        assertThatThrownBy(() -> timerService.startTimer(request, userId))
                .isInstanceOf(InvalidStudySubjectException.class);
    }

    @Test
    void 타이머를_종료한다() {
        Long userId = 1L;
        Long timerId = 100L;
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);

        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", timerId);

        StudyTime running = StudyTime.builder()
                .userId(userId)
                .studySubjectId(10L)
                .startTime(startTime)
                .endTime(null)
                .build();
        ReflectionTestUtils.setField(running, "id", timerId);

        given(studyTimeRepository.findByIdForUpdate(timerId)).willReturn(Optional.of(running));
        given(dailyStudySummaryRepository.findByUserIdAndDateForUpdate(eq(userId), any())).willReturn(Optional.empty());
        given(dailyStudySummaryRepository.save(any(DailyStudySummary.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        PostTimerStopResponse response = timerService.stopTimer(request, userId);

        assertThat(response.getTimerId()).isEqualTo(timerId);
        assertThat(response.getStartTime()).isEqualTo(startTime);
        assertThat(response.getEndTime()).isNotNull();
    }

    @Test
    void 타이머_소유자가_아니면_종료_시_예외가_발생한다() {
        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", 100L);

        StudyTime running = StudyTime.builder()
                .userId(2L)
                .studySubjectId(10L)
                .startTime(java.time.LocalDateTime.of(2026, 2, 28, 10, 0, 0))
                .endTime(null)
                .build();
        ReflectionTestUtils.setField(running, "id", 100L);

        given(studyTimeRepository.findByIdForUpdate(100L)).willReturn(Optional.of(running));

        assertThatThrownBy(() -> timerService.stopTimer(request, 1L))
                .isInstanceOf(TimerNotOwnedException.class);
    }

    @Test
    void 이미_종료된_타이머를_종료하면_예외가_발생한다() {
        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", 100L);

        StudyTime stopped = StudyTime.builder()
                .userId(1L)
                .studySubjectId(10L)
                .startTime(java.time.LocalDateTime.of(2026, 2, 28, 10, 0, 0))
                .endTime(java.time.LocalDateTime.of(2026, 2, 28, 11, 0, 0))
                .build();
        ReflectionTestUtils.setField(stopped, "id", 100L);

        given(studyTimeRepository.findByIdForUpdate(100L)).willReturn(Optional.of(stopped));

        assertThatThrownBy(() -> timerService.stopTimer(request, 1L))
                .isInstanceOf(TimerAlreadyStoppedException.class);
    }

    @Test
    void 실행_중인_타이머를_조회한다() {
        Long userId = 1L;
        StudyTime running = StudyTime.builder()
                .userId(userId)
                .studySubjectId(10L)
                .startTime(java.time.LocalDateTime.of(2026, 2, 28, 10, 0, 0))
                .endTime(null)
                .build();
        ReflectionTestUtils.setField(running, "id", 100L);

        given(studyTimeRepository.findByUserIdAndEndTimeIsNull(userId)).willReturn(Optional.of(running));

        GetRunningTimerResponse response = timerService.findRunningTimer(userId);

        assertThat(response).isNotNull();
        assertThat(response.getTimerId()).isEqualTo(100L);
        assertThat(response.getSubjectId()).isEqualTo(10L);
        assertThat(response.getStartTime()).isEqualTo(java.time.LocalDateTime.of(2026, 2, 28, 10, 0, 0));
    }

    @Test
    void 실행_중인_타이머가_없으면_null을_반환한다() {
        given(studyTimeRepository.findByUserIdAndEndTimeIsNull(1L)).willReturn(Optional.empty());

        GetRunningTimerResponse response = timerService.findRunningTimer(1L);

        assertThat(response).isNull();
    }

    @Test
    void 오늘_과목별_누적_공부시간을_조회한다() {
        Long userId = 1L;
        StudySubject subject = StudySubject.builder()
                .userId(userId)
                .name("수학")
                .color("파란색")
                .orderIndex(1L)
                .build();
        ReflectionTestUtils.setField(subject, "id", 10L);

        given(studySubjectRepository.findAllByUserId(userId)).willReturn(List.of(subject));
        given(studyTimeRepository.findDailyStudyTimeBySubject(org.mockito.ArgumentMatchers.eq(userId), any(), any()))
                .willReturn(java.util.Collections.singletonList(new Object[]{10L, 2400L}));

        List<SubjectStudyTimeItemResponse> response = timerService.findTodaySubjectStudyTimes(userId);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getSubjectId()).isEqualTo(10L);
        assertThat(response.get(0).getSubjectName()).isEqualTo("수학");
        assertThat(response.get(0).getStudyTime()).isEqualTo(2400L);
    }

    @Test
    void 오늘_총_공부시간을_조회한다() {
        Long userId = 1L;
        given(studyTimeRepository.sumDailyStudyTimeByUserId(org.mockito.ArgumentMatchers.eq(userId), any(), any()))
                .willReturn(2400L);

        GetTimerTotalResponse response = timerService.findTodayTotalStudyTime(userId);

        assertThat(response.getStudyTime()).isEqualTo(2400L);
    }

    @Test
    void 기존_일일_요약은_락을_획득한_후_업데이트한다() {
        Long userId = 1L;
        Long timerId = 100L;
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);

        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", timerId);

        StudyTime running = StudyTime.builder()
                .userId(userId)
                .studySubjectId(10L)
                .startTime(startTime)
                .endTime(null)
                .build();
        ReflectionTestUtils.setField(running, "id", timerId);

        DailyStudySummary summary = DailyStudySummary.builder()
                .userId(userId)
                .studyTime(100L)
                .date(startTime.toLocalDate())
                .build();

        given(studyTimeRepository.findByIdForUpdate(timerId)).willReturn(Optional.of(running));
        given(dailyStudySummaryRepository.findByUserIdAndDateForUpdate(eq(userId), any()))
                .willReturn(Optional.of(summary));
        given(dailyStudySummaryRepository.save(any(DailyStudySummary.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        timerService.stopTimer(request, userId);

        assertThat(summary.getStudyTime()).isGreaterThan(100L);
    }

    @Test
    void 타이머_종료_시_5시를_넘기면_일일_요약을_스터디데이별로_나눠_저장한다() {
        Long userId = 1L;
        Long timerId = 100L;
        LocalDateTime studyDayStart = TimeUtil.startOfStudyDay(LocalDateTime.now());
        LocalDate previousStudyDate = studyDayStart.minusDays(1).toLocalDate();
        LocalDate currentStudyDate = studyDayStart.toLocalDate();
        LocalDateTime startTime = studyDayStart.minusHours(1);

        PostTimerStopRequest request = new PostTimerStopRequest();
        ReflectionTestUtils.setField(request, "timerId", timerId);

        StudyTime running = StudyTime.builder()
                .userId(userId)
                .studySubjectId(10L)
                .startTime(startTime)
                .endTime(null)
                .build();
        ReflectionTestUtils.setField(running, "id", timerId);

        DailyStudySummary previousDaySummary = DailyStudySummary.builder()
                .userId(userId)
                .studyTime(0L)
                .date(previousStudyDate)
                .build();

        DailyStudySummary currentDaySummary = DailyStudySummary.builder()
                .userId(userId)
                .studyTime(0L)
                .date(currentStudyDate)
                .build();

        given(studyTimeRepository.findByIdForUpdate(timerId)).willReturn(Optional.of(running));
        given(dailyStudySummaryRepository.findByUserIdAndDateForUpdate(userId, previousStudyDate))
                .willReturn(Optional.of(previousDaySummary));
        given(dailyStudySummaryRepository.findByUserIdAndDateForUpdate(userId, currentStudyDate))
                .willReturn(Optional.of(currentDaySummary));
        given(dailyStudySummaryRepository.save(any(DailyStudySummary.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        timerService.stopTimer(request, userId);

        assertThat(previousDaySummary.getStudyTime()).isEqualTo(3600L);
        assertThat(currentDaySummary.getStudyTime()).isGreaterThanOrEqualTo(0L);
        verify(dailyStudySummaryRepository).findByUserIdAndDateForUpdate(userId, previousStudyDate);
        verify(dailyStudySummaryRepository).findByUserIdAndDateForUpdate(userId, currentStudyDate);
    }

}
