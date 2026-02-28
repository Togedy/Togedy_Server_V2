package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyRunningException;
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
        given(studyTimeRepository.save(any(StudyTime.class))).willReturn(saved);

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
}
