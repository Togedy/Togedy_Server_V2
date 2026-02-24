package com.togedy.togedy_server_v2.domain.planner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyTimetableResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StudyTimeServiceTest {

    @Mock
    private StudySubjectRepository studySubjectRepository;

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @InjectMocks
    private StudyTimeService studyTimeService;

    @Test
    void 일별_타임테이블_조회_시_오전_5시_기준으로_조회한다() {
        Long userId = 1L;
        LocalDate queryDate = LocalDate.of(2026, 2, 17);

        StudySubject english = StudySubject.builder()
                .userId(userId)
                .name("영어")
                .color("노란색")
                .orderIndex(1L)
                .build();
        ReflectionTestUtils.setField(english, "id", 11L);

        StudyTime first = org.mockito.Mockito.mock(StudyTime.class);
        StudyTime second = org.mockito.Mockito.mock(StudyTime.class);
        given(first.getStudySubjectId()).willReturn(11L);
        given(first.getStartTime()).willReturn(LocalDateTime.of(2026, 2, 17, 6, 12, 5));
        given(first.getEndTime()).willReturn(LocalDateTime.of(2026, 2, 17, 6, 30, 0));
        given(second.getStudySubjectId()).willReturn(11L);
        given(second.getStartTime()).willReturn(LocalDateTime.of(2026, 2, 18, 4, 12, 5));
        given(second.getEndTime()).willReturn(LocalDateTime.of(2026, 2, 18, 4, 30, 0));

        given(studySubjectRepository.findAllByUserId(userId)).willReturn(List.of(english));
        given(studyTimeRepository.findDailyStudyTimesBySubjectIds(
                List.of(11L),
                LocalDateTime.of(2026, 2, 17, 5, 0, 0),
                LocalDateTime.of(2026, 2, 18, 5, 0, 0)
        )).willReturn(List.of(second, first));

        GetDailyTimetableResponse response = studyTimeService.findDailyTimetables(queryDate, userId);

        assertThat(response.getTimeTableList()).hasSize(2);
        assertThat(response.getTimeTableList().get(0).getStartTime()).isEqualTo(LocalTime.of(6, 12, 5));
        assertThat(response.getTimeTableList().get(0).getEndTime()).isEqualTo(LocalTime.of(6, 30, 0));
        assertThat(response.getTimeTableList().get(0).getSubjectColor()).isEqualTo("노란색");
        assertThat(response.getTimeTableList().get(1).getStartTime()).isEqualTo(LocalTime.of(4, 12, 5));
        assertThat(response.getTimeTableList().get(1).getEndTime()).isEqualTo(LocalTime.of(4, 30, 0));
    }
}
